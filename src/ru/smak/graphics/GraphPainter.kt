package ru.smak.graphics

import java.awt.*
import kotlin.math.*

class GraphPainter(
        val graph: MutableList<MutableList<Double>>
) : Painter {

    private var width = 1
    private var height = 1
    var thickness = 1
        set(value) {
            if (value >=1 && value <= 30) {
                field = value
                calcVertexPositions()
            }
        }

    var vertexSize = 30
        set(value){
            if (value >=10 && value <= 100) {
                field = value
                calcVertexPositions()
            }
        }

    override var size: Dimension
    get() = Dimension(width, height)
    set(value){
        width = value.width
        height = value.height
        calcVertexPositions()
    }

    private val minSz: Int
        get() = min(width, height) - vertexSize - thickness

    private val rect: Rectangle
        get() = Rectangle((width - minSz)/2, (height-minSz)/2, minSz, minSz)

    private val radius: Int
        get() = minSz / 2

    private val center: Point
        get() = Point(rect.x + radius, rect.y + radius)

    private val phi: Double
        get() = 2 * PI / graph.size

    private var vertexPositions: MutableList<Point>? = null

    private fun calcVertexPositions(){
        vertexPositions = MutableList<Point>(graph.size) { i ->
            Point((center.x + radius * cos(i * phi)).toInt() ,
                    (center.y + radius * sin(i * phi)).toInt()
            )
        }
    }

    override fun paint(g: Graphics){
        paintEdges(g)
        paintVerticies(g)
    }

    private fun paintVerticies(g: Graphics) {
        (g as Graphics2D).apply {
            //rotate(-PI / 2, center.x.toDouble(), center.y.toDouble())
            var z: Int= 1
            vertexPositions?.forEach {
                g.color = Color.WHITE
                g.fillOval(it.x - vertexSize / 2, it.y - vertexSize / 2, vertexSize, vertexSize)
                g.color = Color.BLUE
                g.drawOval(it.x - vertexSize / 2, it.y - vertexSize / 2, vertexSize, vertexSize)
                paintNumbers(g,z,it.x,it.y )
                //g.drawString(z.toString(),it.x + vertexSize/64,it.y + vertexSize/8)
                z=z+1
            }
        }
    }

    private fun paintEdges(g: Graphics) {
        (g as Graphics2D).apply {
            rotate(-PI / 2, center.x.toDouble(), center.y.toDouble())
            stroke = BasicStroke(
                    thickness.toFloat(),
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND)
            setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            )
        }

        graph.forEachIndexed { fromInd, from ->
            from.takeLast(graph.size - fromInd - 1)
                    .forEachIndexed { toInd, weight ->
                if (weight > 1e-20) {
                    vertexPositions?.let { vPos ->
                        val toI = toInd + fromInd + 1
                        g.drawLine(
                                vPos[fromInd].x, vPos[fromInd].y,
                                vPos[toI].x, vPos[toI].y
                        )
                        //g.drawString(weight.toString(),(vPos[fromInd].x+vPos[toI].x)/2,(vPos[fromInd].y+vPos[toI].y)/2)
                        val phi = Math.acos((vPos[toI].x-vPos[fromInd].x).toDouble()/Math.sqrt(Math.pow((vPos[toI].x-vPos[fromInd].x).toDouble(),2.0)+Math.pow((-vPos[fromInd].y+vPos[toI].y).toDouble(),2.0)))
                        val phi2 = Math.atan((-vPos[fromInd].y+vPos[toI].y).toDouble()/(vPos[toI].x-vPos[fromInd].x).toDouble())
                        paintWeights(g,weight,vPos[fromInd].x,vPos[fromInd].y,vPos[toI].x,vPos[toI].y,phi2)
                    }
                }
            }
        }
        /*for (i in 0 until graph.size - 1){
            for (j in i+1 until graph.size) {
                if (graph[i][j]>1e-20){
                    vertexPositions?.let { vp ->
                        g.drawLine(vp[i].x, vp[i].y, vp[j].x, vp[j].y)
                    }
                }
            }
        }*/
    }
    private fun paintNumbers(g:Graphics,num:Int,x:Int,y:Int){
        (g.create() as Graphics2D).apply {
            color = Color.MAGENTA
            rotate(PI/2,x.toDouble(), y.toDouble())
            if(num/10>0) {
                drawString(num.toString(), x - 6, y + 4)
            }else{
                drawString(num.toString(), x - 4, y + 4)
            }
        }
    }

    private fun paintWeights(g:Graphics2D,weight:Double,x1:Int,y1:Int,x2:Int,y2:Int,phi:Double){
        (g.create() as Graphics2D).apply {
            color = Color.red
            val length = Math.sqrt((x2-x1).toDouble()*(x2-x1).toDouble()+(y2-y1).toDouble()*(y2-y1).toDouble())
            if(phi<Math.PI && phi>0){
                rotate(phi,((x1+x2)/2).toDouble(),((y1+y2)/2).toDouble())
            }else {
                rotate(phi+Math.PI, ((x1 + x2) / 2).toDouble(), ((y1 + y2) / 2).toDouble())
            }
            print(phi*180/Math.PI)
            println(" "+weight)

            drawString(weight.toString(),(x1+x2)/2-((x2-x1)/length).toInt(),(y1+y2)/2-100*((y2-y1)/length).toInt())

        }
    }
}