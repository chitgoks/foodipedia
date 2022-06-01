package org.lifesum.foodipedia

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.lifesum.foodipedia.model.Food
import org.lifesum.foodipedia.ui.theme.AvenirLight
import org.lifesum.foodipedia.ui.theme.FoodipediaTheme
import org.lifesum.foodipedia.util.gradientBackground
import org.lifesum.foodipedia.web.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.sqrt

// https://blog.kotlin-academy.com/jetpack-compose-state-management-73ec3f6c74a5
object AppMain {
    var isLoading: Boolean by mutableStateOf(false)
    var food: Food by mutableStateOf(Food())
}

class MainActivity : ComponentActivity(), SensorEventListener  {
    // https://www.geeksforgeeks.org/how-to-detect-shake-event-in-android/
    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager?.registerListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH

        setContent {
            FoodipediaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Main(appState())
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        // Fetching x,y,z values
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        lastAcceleration = currentAcceleration

        // Getting current accelerations
        // with the help of fetched x,y,z values
        currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val delta: Float = currentAcceleration - lastAcceleration
        acceleration = acceleration * 0.9f + delta

        // Display a Toast message if
        // acceleration value is over 12
        if (acceleration > 12)
            getRandomFood()
    }

    override fun onResume() {
        sensorManager?.registerListener(this, sensorManager!!.getDefaultSensor(
            Sensor .TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }

    override fun onPause() {
        sensorManager!!.unregisterListener(this)
        super.onPause()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    private fun getRandomFood() {
        AppMain.isLoading = true

        val apiInterface = ApiInterface.create().getFood(ApiInterface.AUTHORIZATION, (1..200).shuffled().last())
        apiInterface.enqueue(object : Callback<org.lifesum.foodipedia.model.Data> {
            override fun onResponse(
                call: Call<org.lifesum.foodipedia.model.Data>,
                data: Response<org.lifesum.foodipedia.model.Data>
            ) {
                AppMain.isLoading = false
                AppMain.food = data.body()!!.response
            }

            override fun onFailure(
                call: Call<org.lifesum.foodipedia.model.Data>,
                t: Throwable
            ) {
                AppMain.isLoading = false
                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}

@Composable
fun Main(appMain: AppMain) {
    val context = LocalContext.current
    Box(contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .gradientBackground(
                        listOf(
                            Color(243, 167, 78, 255),
                            Color(237, 84, 96, 255)
                        ), -30f
                    )
                    .width(271.6.dp)
                    .height(271.6.dp),
                contentAlignment = Alignment.Center
            )
            {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth(fraction = 0.7f))
                {
                    MainLabel(appMain.food.title, 20, true)
                    MainLabel(appMain.food.calories.toString(), 70)
                    MainLabel(label = "Calories per serving", fontSize = 20, underline = false, italicize = true)
                }
            }
            Spacer(Modifier.height(50.0.dp))
            Row {
                Column {
                    IntakeLabel("CARBS")
                    DividerIntake()
                    IntakeLabel(appMain.food.carbs.toBigDecimal().toPlainString() + "%")
                }
                Spacer(Modifier.width(5.dp))
                Column {
                    IntakeLabel("PROTEIN")
                    DividerIntake()
                    IntakeLabel(appMain.food.protein.toBigDecimal().toPlainString() + "%")
                }
                Spacer(Modifier.width(5.dp))
                Column {
                    IntakeLabel("FAT")
                    DividerIntake()
                    IntakeLabel(appMain.food.fat.toBigDecimal().toPlainString() + "%")
                }
            }
            Spacer(Modifier.height(100.0.dp))
            Button(
                modifier = Modifier.size(width = 287.dp, height = 75.dp),
                shape = RoundedCornerShape(100),
                colors = ButtonDefaults.buttonColors(
                    // TODO: background color array fill?
                    backgroundColor = Color(1, 5, 3, 255),
                    contentColor = Color.White
                ),
                content = {
                    Text(
                        text = "MORE INFO",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal
                    )
                },
                onClick = {
                    Toast.makeText(context, "Nothing doing ...", Toast.LENGTH_SHORT).show()
                },
            )
        }
        if (appMain.isLoading) CircularProgressIndicator()
    }
}

@Composable
fun DividerIntake() {
    Divider(
        modifier = Modifier
            .width(99.6.dp)
            .padding(10.dp),
        color = Color(225, 226, 226)
    )
}

@Composable
fun IntakeLabel(label: String) {
    Text(label,
        color = Color(108, 108, 108),
        fontFamily = AvenirLight,
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .width(99.6.dp)
            .height(19.dp)
    )
}

@Composable
fun MainLabel(label: String, fontSize: Int, underline: Boolean = false, italicize: Boolean = false) {
    Text(
        label,
        color = Color.White,
        fontFamily = AvenirLight,
        fontSize = fontSize.sp,
        fontStyle = if (italicize) FontStyle.Italic else FontStyle.Normal,
        textAlign = TextAlign.Center,
        style = TextStyle(
            textDecoration = if (underline) TextDecoration.Underline else TextDecoration.None
        )
    )
}

@Composable
fun appState(): AppMain {
    return remember { AppMain }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FoodipediaTheme {
        Main(appState())
    }
}