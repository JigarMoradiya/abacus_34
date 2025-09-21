package com.jigar.me.data.local.data

import android.graphics.drawable.Drawable
import com.jigar.me.R

enum class EventBusType {
    LoginSync
}
data class MessageEvent(
    val type: EventBusType
)
data class ColorData(val color: Int, val darkColor: Int, val bgColor : Int? = null)
data class HomeBanner(val type: String,val title : String, val text : String,val btnText : String, val background : Drawable?)
data class HomeMenu(val type: Int, val image: Int,val tag : String = "")
data class MyAccountMenu(
    val tag: String,
    val menuTitle: String,
    val menuIcon: Int? = null,
    val subMenu: List<MyAccountMenu> = arrayListOf(),
    val isPaid: Boolean = false,
)
data class AvatarImages(val id: Int, val image: Int)
data class FAQs(val question: String, val answer: String)
data class VideoTutorial(val videoName: String, val title: String,val pointsList : ArrayList<String>)

data class AbacusContent(val type: String, val beadImage: Int, val abacusFrame135 : Int, val abacusFrameExam135 : Int,
                         val dividerColor1 : Int, val resetBtnColor8 : Int, val beadHeight : Int, val beadWidth : Int,
                         val beadSpace : Int, val topBeadClose : Int, val topBeadOpen : Int, var bottomBeadClose: ArrayList<Int>,
                         var bottomBeadOpen: ArrayList<Int>, val txtColor : Int? = null, val unUsedBeads : Int,
                         val answerWindowBG : String = "#283593", val answerWindowLine : String = "FFB300", val answerWindowBtnBgLine : String = "FFB300")
data class OtherApps(val type: Int, val image: Int,val name : String, val url : String)
data class ImagesDataObjects(val type: DataObjectsType, val name: String, val image: String)
data class BeginnerExamPaper(val type: BeginnerExamQuestionType, val value: String,val value2: String, val imageData: ImagesDataObjects? = null, var userAnswer : String? = "", var isAbacusQuestion : Boolean? = false)

data class ExerciseLevel(val id : String, val title: String, val list : ArrayList<ExerciseLevelDetail>,var selectedChildPos : Int = 0)
data class ExerciseLevelDetail(val id : String,val totalQue : Int,val queLines : Int,val digits : Int, val totalTime: Int)
data class ExerciseList(val question : String, val answer : String, var userAnswer : String? = null)
data class CustomChallengeData(val questions : MutableList<CustomChallengeQuestion>, val fullQuestion : String,val answer : Int)
data class CustomChallengeQuestion(val sign : String, val question : Int)

data class Movement(
    val upperUp: Boolean = false,
    val upperDown: Boolean = false,
    val lowerUp: Int = 0,    // number of lower beads moved up
    val lowerDown: Int = 0,   // number of lower beads moved down
    val lowerOldValue: Int = 0,   // number of lower beads existing value
)

data class RodMovement(
    val rodIndex: Int,
    val movement: Movement
)
