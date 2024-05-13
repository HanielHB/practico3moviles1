package com.example.practico3

import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practico3.uiltel.DeslizamientosListener
import java.util.Arrays.asList

class MainActivity : AppCompatActivity() {

    /*Añadir los dulces*/
    var candies = intArrayOf(
        R.drawable.bluecandy,
        R.drawable.greencandy,
        R.drawable.orangecandy,
        R.drawable.purplecandy,
        R.drawable.redcandy,
        R.drawable.yellowcandy
    )
    var anchoBloque: Int = 0
    var noDeBloques: Int = 8
    var anchoPantalla: Int = 0
    lateinit var candy :ArrayList<ImageView>
    var candyArrastrado : Int = 0
    var candyReemplazado : Int = 0
    var sinCandy : Int = R.drawable.transparent

    lateinit var mHandler : Handler
    private lateinit var scoreReult : TextView
    var score = 0
    val interval = 100L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        scoreReult = findViewById(R.id.score)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        anchoPantalla = displayMetrics.widthPixels

        var heightOfScreen = displayMetrics.heightPixels

        anchoBloque = anchoPantalla / noDeBloques

        candy = ArrayList()
        createBoard()

        for (imageView in candy){
            imageView.setOnTouchListener(
                object :DeslizamientosListener(this) {
                    override fun onSwipeRight() {
                        super.onSwipeRight()
                        candyArrastrado = imageView.id
                        candyReemplazado = candyArrastrado + 1
                        if (candyReemplazado % noDeBloques > 0) { // Check if not on the right edge
                            candyIntercambiables()
                        }
                    }

                    override fun onSwipeLeft() {
                        super.onSwipeLeft()
                        candyArrastrado = imageView.id
                        candyReemplazado = candyArrastrado - 1
                        if (candyArrastrado % noDeBloques > 0) { // Check if not on the left edge
                            candyIntercambiables()
                        }
                    }

                    override fun onSwipeTop() {
                        super.onSwipeTop()
                        candyArrastrado = imageView.id
                        candyReemplazado = candyArrastrado - noDeBloques
                        if (candyArrastrado >= noDeBloques) { // Check if not on the top edge
                            candyIntercambiables()
                        }
                    }

                    override fun onSwipeBottom() {
                        super.onSwipeBottom()
                        candyArrastrado = imageView.id
                        candyReemplazado = candyArrastrado + noDeBloques
                        if (candyReemplazado < noDeBloques * noDeBloques) { // Check if not on the bottom edge
                            candyIntercambiables()
                        }
                    }
                })

        }

        mHandler = Handler()
        startRepeat()
    }





    private fun candyIntercambiables() {

        var background :Int = candy.get(candyReemplazado).tag as Int
        var background1 :Int = candy.get(candyArrastrado).tag as Int

        candy.get(candyReemplazado).setImageResource(background1)
        candy.get(candyArrastrado).setImageResource(background)

        candy.get(candyArrastrado).setTag(background)
        candy.get(candyReemplazado).setTag(background1)
    }


    private fun checkRowForThree(){
        for (i in 0 until noDeBloques * (noDeBloques - 2)) {
            var chosenCandy = candy[i].tag as Int
            var isBlank :Boolean = candy[i].tag == sinCandy
            var count = 0
            var x = i
            while (x < candy.size && candy[x].tag as Int == chosenCandy && !isBlank) {
                count++
                x++
            }
            if (count >= 3) {
                score += count
                scoreReult.text = "$score"
                for (j in 0 until count) {
                    x--
                    candy[x].setImageResource(sinCandy)
                    candy[x].setTag(sinCandy)
                }
            }
        }
        moveDownCandies()
    }

    private fun checkColumnForThree(){
        for (i in 0..47) {
            var chosedCandy = candy.get(i).tag
            var isBlank :Boolean = candy.get(i).tag == sinCandy
            var x = i
            var count = 0
            while (x < candy.size && candy.get(x).tag as Int == chosedCandy && !isBlank) {
                count++
                x += noDeBloques
            }
            if (count >= 3) {
                score += count
                scoreReult.text = "$score"
                for (j in 0 until count) {
                    x -= noDeBloques
                    candy.get(x).setImageResource(sinCandy)
                    candy.get(x).setTag(sinCandy)
                }
            }
        }
        moveDownCandies()
    }

    private fun moveDownCandies() {

        val firstRow = arrayOf(1,2,3,4,5,6,7)
        val list = asList(*firstRow)

        for (i in 55 downTo 0){
            if (candy.get(i + noDeBloques).tag as Int == sinCandy){
                
                candy.get(i + noDeBloques).setImageResource(candy.get(i).tag as Int)
                candy.get(i + noDeBloques).setTag(candy.get(i).tag as Int)

                candy.get(i).setImageResource(sinCandy)
                candy.get(i).setTag(sinCandy)

                if (list.contains(i) && candy.get(i).tag == sinCandy){
                    var randomColor :Int = Math.abs(Math.random() * candies.size).toInt()
                    candy.get(i).setImageResource(candies[randomColor])
                    candy.get(i).setTag(candies[randomColor])
                }
            }

        }
        for (i in 0..7){
            if (candy.get(i).tag as Int == sinCandy){
                var randomColor :Int = Math.abs(Math.random() * candies.size).toInt()
                candy.get(i).setImageResource(candies[randomColor])
                candy.get(i).setTag(candies[randomColor])
            }
        }
    }
    val repeatChecker : Runnable = object : Runnable {
        override fun run(){
            try{
                checkRowForThree()
                checkColumnForThree()
                moveDownCandies()
            }
            finally {
                mHandler.postDelayed(this,interval)}
        }
    }
    private fun startRepeat() {
        repeatChecker.run()
    }
    private fun createBoard() {
        val gridLayout = findViewById<GridLayout>(R.id.board)
        gridLayout.rowCount = noDeBloques
        gridLayout.columnCount = noDeBloques
        gridLayout.layoutParams.width = anchoPantalla
        gridLayout.layoutParams.height = anchoPantalla

        for (i in 0 until noDeBloques * noDeBloques){

            val imageView = ImageView(this)
            imageView.id = i
            imageView.layoutParams = android.
            view.ViewGroup.
            LayoutParams(anchoBloque, anchoBloque)

            imageView.maxHeight = anchoBloque
            imageView.maxWidth = anchoBloque

            val random :Int = Math.floor(Math.random() * candies.size).toInt()

            // randomIndex from candies array
            imageView.setImageResource(candies[random])
            imageView.setTag(candies[random])

            candy.add(imageView)
            gridLayout.addView(imageView)

        }
    }



}