package com.gmf.stockopname

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.gmf.stockopname.data.AppViewModel
import com.gmf.stockopname.ui.navigation.AppNavGraph
import com.gmf.stockopname.ui.theme.StockOpnameTheme

class MainActivity : ComponentActivity() {

    private val vm: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StockOpnameTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph(vm = vm)
                }
            }
        }
    }
}
