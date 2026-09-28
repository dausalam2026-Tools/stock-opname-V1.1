"""
Konversi file Master Tools Database (Excel .xlsx atau CSV) menjadi
app/src/main/assets/masterdata.json yang dipakai aplikasi Android.

CARA PAKAI (butuh Python 3 + pip install pandas openpyxl):
    python convert_masterdata.py path/ke/database_baru.xlsx

Kolom yang dicari (harus ada persis salah satu nama ini, tidak peduli
huruf besar/kecil): Status, Tool Number, Part Number, Key Number,
Serial Number, Tool Description, Manufacture, Model, Category, Store,
Location. Kolom lain (Transit Destination, Due Date, dst) diabaikan.

Kalau Anda tidak nyaman menjalankan script ini sendiri, cukup kirim
file database barunya (Excel/CSV/PDF) ke Claude — saya akan jalankan
konversinya dan kirim balik project yang sudah ter-update.
"""
import json
import sys
from pathlib import Path

import pandas as pd

COLUMN_MAP = {
    "status": "status",
    "tool number": "tn",
    "part number": "pn",
    "key number": "kn",
    "serial number": "sn",
    "tool description": "desc",
    "manufacture": "mfr",
    "model": "model",
    "store": "store",
    "location": "location",
}

VALID_STATUS = {
    "available", "calibration", "borrowed", "quarantined",
    "maintenance", "broken", "missing",
}


def load_table(path: Path) -> pd.DataFrame:
    if path.suffix.lower() in (".xlsx", ".xls"):
        return pd.read_excel(path)
    return pd.read_csv(path)


def normalize_columns(df: pd.DataFrame) -> pd.DataFrame:
    lower_map = {c.strip().lower(): c for c in df.columns}
    rename = {}
    missing = []
    for wanted_lower, short in COLUMN_MAP.items():
        if wanted_lower in lower_map:
            rename[lower_map[wanted_lower]] = short
        else:
            missing.append(wanted_lower)
    if missing:
        print(f"PERINGATAN: kolom berikut tidak ditemukan dan akan dikosongkan: {missing}")
    df = df.rename(columns=rename)
    for short in COLUMN_MAP.values():
        if short not in df.columns:
            df[short] = ""
    return df


def norm(v) -> str:
    if pd.isna(v):
        return ""
    return " ".join(str(v).strip().split())


def main():
    if len(sys.argv) < 2:
        print("Pemakaian: python convert_masterdata.py path/ke/file.xlsx")
        sys.exit(1)

    src = Path(sys.argv[1])
    if not src.exists():
        print(f"File tidak ditemukan: {src}")
        sys.exit(1)

    df = load_table(src)
    df = normalize_columns(df)

    df["store"] = df["store"].apply(norm)
    df["location"] = df["location"].apply(norm)
    df["status"] = df["status"].apply(lambda v: norm(v).lower())

    before = len(df)
    df = df[(df["store"] != "") & (df["location"] != "")]
    unknown_status = sorted(set(df["status"]) - VALID_STATUS - {""})
    if unknown_status:
        print(f"PERINGATAN: status tidak dikenal ditemukan (tetap disimpan apa adanya): {unknown_status}")
    print(f"Baris valid: {len(df)} dari {before} baris mentah.")

    data = {}
    for store, g_store in df.groupby("store"):
        locs = {}
        for loc, g_loc in g_store.groupby("location"):
            tools = []
            for _, r in g_loc.iterrows():
                tools.append({
                    "tn": norm(r["tn"]),
                    "pn": norm(r["pn"]),
                    "kn": norm(r["kn"]),
                    "sn": norm(r["sn"]) or "N/A",
                    "desc": norm(r["desc"]),
                    "mfr": norm(r["mfr"]),
                    "model": norm(r["model"]) or "N/A",
                    "status": norm(r["status"]) or "available",
                })
            locs[loc] = tools
        data[store] = locs

    stores_found = sorted(data.keys())
    print(f"Store ditemukan: {stores_found}")
    print("-> Kalau ada store BARU di luar W1/W2/LG, tambahkan labelnya sendiri")
    print("   di app/src/main/java/com/gmf/stockopname/data/Models.kt, di map STORE_LABELS,")
    print("   supaya tampil dengan nama rapi (kalau tidak, kode store mentah tetap tampil, tidak akan error).")

    out_path = Path(__file__).resolve().parent.parent / "app" / "src" / "main" / "assets" / "masterdata.json"
    out_path.parent.mkdir(parents=True, exist_ok=True)
    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, separators=(",", ":"))

    total_tools = sum(len(t) for locs in data.values() for t in locs.values())
    print(f"\nSelesai. {total_tools} tool tersimpan ke:\n{out_path}")
    print("Lanjutkan seperti biasa: commit + push lewat GitHub Desktop, lalu build ulang di tab Actions.")


if __name__ == "__main__":
    main()
