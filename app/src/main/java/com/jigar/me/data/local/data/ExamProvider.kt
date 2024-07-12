package com.jigar.me.data.local.data

import android.content.Context
import android.util.Log
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Calculator
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Constants

object ExamProvider {
    fun generateExamPaperNew(examLevel : String,list : ArrayList<String>) : List<BeginnerExamPaper>{
        var totalQuestion = 10
        val paperList : ArrayList<BeginnerExamPaper> = arrayListOf()
        when (list.size) {
            3 -> {
                totalQuestion = 15
            }
            4 -> {
                totalQuestion = 20
            }
            5 -> {
                totalQuestion = 25
            }
        }
        for (ii in 0 until totalQuestion) {
            if (list.isNotEmpty()){
                list.shuffle()
                if (list.first().equals(AppConstants.ExamType.exam_Type_Number,true)){
                    paperList.add(generateExamNumber(examLevel))
                }else if (list.first().equals(AppConstants.ExamType.exam_Type_Addition,true)){
                    paperList.add(generateExamAddition(examLevel))
                }else if (list.first().equals(AppConstants.ExamType.exam_Type_Subtraction,true)){
                    paperList.add(generateExamSubtraction(examLevel))
                }else if (list.first().equals(AppConstants.ExamType.exam_Type_Multiplication,true)){
                    paperList.add(generateExamMultiplicationDivision(examLevel))
                }else if (list.first().equals(AppConstants.ExamType.exam_Type_Division,true)){
                    paperList.add(generateExamMultiplicationDivision(examLevel,true))
                }
            }else{
                paperList.add(generateExamAddition(examLevel))
            }

        }
        return paperList
    }
    private fun generateExamNumber(examLevel : String) : BeginnerExamPaper{
        var min = 1
        var max = 16
//        when (examLevel) {
//            AppConstants.ExamType.exam_Level_Beginner -> {
//                min = 1
//                max = 100
//            }
//            AppConstants.ExamType.exam_Level_Intermediate -> {
//                min = 101
//                max = 1000
//            }
//            AppConstants.ExamType.exam_Level_Expert -> {
//                min = 500
//                max = 100001
//            }
//        }
//        min = 9990
//        max = 10010
        val number = DataProvider.generateSingleDigit(min, max)
        // TODO comment abacus question
        val listDataObjects = getDataObjectsList()
        return BeginnerExamPaper(BeginnerExamQuestionType.Count,number.toString(),"",listDataObjects.first())
//        return if (number < 15){
//            val listDataObjects = getDataObjectsList()
//            BeginnerExamPaper(BeginnerExamQuestionType.Count,number.toString(),"",listDataObjects.first())
//        }else{
//            BeginnerExamPaper(BeginnerExamQuestionType.Count,number.toString(),"",null, isAbacusQuestion = true)
//        }
    }
    private fun generateExamAddition(examLevel : String) : BeginnerExamPaper{
        var min = 1
        var max = 9
        var totalQuestion = 1
        var maxLength = 1
        if (examLevel == AppConstants.ExamType.exam_Level_Expert){
            min = 9
            max = 10000
            totalQuestion = DataProvider.generateSingleDigit(1, 4)
            var mainQuestion = DataProvider.generateSingleDigit(min, max).toString()
            for (ii in 0 until totalQuestion) {
                mainQuestion += "+" + DataProvider.generateSingleDigit(min, max).toString()
            }
            return BeginnerExamPaper(BeginnerExamQuestionType.Additions,mainQuestion,"",null, isAbacusQuestion = false)
        }else{
            when (examLevel) {
                AppConstants.ExamType.exam_Level_Beginner -> {
                    min = 1
                    max = 9
                    totalQuestion = 1
                    maxLength = 1
                }
                AppConstants.ExamType.exam_Level_Intermediate -> {
                    min = 11
                    max = 1000
                    totalQuestion = DataProvider.generateSingleDigit(1, 3)
                    maxLength = 3
                }
            }
            val questionList: ArrayList<Int> = arrayListOf()
            var answer: String = DataProvider.generateSingleDigit(min, max).toString()
            var mainQuestion: String = answer
            var firstQue: String = answer
            questionList.add(answer.toInt())
            for (ii in 0 until totalQuestion) {
                if (answer.length != maxLength) {
                    for (i in 0 until (maxLength - answer.length)) {
                        answer = "0$answer"
                    }
                }
                val questionLength =when (examLevel) {
                    AppConstants.ExamType.exam_Level_Beginner -> {
                        DataProvider.generateSingleDigit(1, 1)
                    }
                    AppConstants.ExamType.exam_Level_Intermediate -> {
                        DataProvider.generateSingleDigit(2, 3)
                    }
                    else -> {
                        DataProvider.generateSingleDigit(1, 1)
                    }
                }
                var newQuestion = ""
                if (questionLength != maxLength) {
                    for (i in 0 until (maxLength - questionLength)) {
                        newQuestion += "0"
                    }
                }
                for (i in newQuestion.length until maxLength) {
                    val list = AbacusProvider.numberListForAddition(answer[i].toString().toInt())
                    newQuestion += list.first()
                }

                if (newQuestion.toInt() != 0) {
                    questionList.add(newQuestion.toInt())
                    mainQuestion = "$mainQuestion+${newQuestion.toInt()}"
                    answer = (answer.toInt() + newQuestion.toInt()).toString()
                }
            }

            if (questionList.size == 1) {
                return generateExamAddition(examLevel)
            }

            return if (firstQue == answer){
                BeginnerExamPaper(BeginnerExamQuestionType.Additions,mainQuestion,"",null, isAbacusQuestion = false)
            }else if (questionList.size == 2){
                if (examLevel == AppConstants.ExamType.exam_Level_Beginner){
                    if (mainQuestion.contains("+")){
                        val listDataObjects = getDataObjectsList()
                        val list = mainQuestion.split("+")
                        BeginnerExamPaper(BeginnerExamQuestionType.Additions,list[0],list[1],listDataObjects.first(), isAbacusQuestion = false)
                        // TODO comment abacus question
//                        if (DataProvider.generateSingleDigit(0, 1) == 1){
//                            BeginnerExamPaper(BeginnerExamQuestionType.Additions,list[0],list[1],listDataObjects.first(), isAbacusQuestion = false)
//                        }else{
//                            BeginnerExamPaper(BeginnerExamQuestionType.Additions,list[0],list[1],null, isAbacusQuestion = true)
//                        }
                    }else{
                        BeginnerExamPaper(BeginnerExamQuestionType.Additions,mainQuestion,"",null, isAbacusQuestion = false)
                    }

                }else{
                    BeginnerExamPaper(BeginnerExamQuestionType.Additions,mainQuestion,"",null, isAbacusQuestion = false)
                }
            }else{
                BeginnerExamPaper(BeginnerExamQuestionType.Additions,mainQuestion,"",null, isAbacusQuestion = false)
            }
        }


    }
    private fun generateExamSubtraction(examLevel : String) : BeginnerExamPaper{
        var min = 1
        var max = 9
        var totalQuestion = 1
        var maxLength = 1
        if (examLevel == AppConstants.ExamType.exam_Level_Expert){
            min = 9
            max = 10000
            totalQuestion = DataProvider.generateSingleDigit(1, 4)
            var answer = DataProvider.generateSingleDigit(min, max)
            var mainQuestion = answer.toString()
            var isMinusDone = false
            for (ii in 0 until totalQuestion) {
                val sum = DataProvider.generateSingleDigit(min, max)
                if (ii == (totalQuestion - 1) && !isMinusDone){
                    mainQuestion = if (sum > answer){
                        "${sum}-${answer}"
                    }else{
                        "${answer}-${sum}"
                    }
                }else{
                    if (sum > answer){
                        answer += sum
                        mainQuestion += "+$sum"
                    }else if (DataProvider.generateSingleDigit(0, 1) == 0){
                        answer -= sum
                        mainQuestion += "-$sum"
                        isMinusDone = true
                    }else{
                        answer += sum
                        mainQuestion += "+$sum"
                    }
                }

            }
            return BeginnerExamPaper(BeginnerExamQuestionType.Subtractions,mainQuestion,"",null, isAbacusQuestion = false)
        }else{
            when (examLevel) {
                AppConstants.ExamType.exam_Level_Beginner -> {
                    min = 1
                    max = 49
                    totalQuestion = 1
                    maxLength = 2
                }
                AppConstants.ExamType.exam_Level_Intermediate -> {
                    min = 11
                    max = 1000
                    totalQuestion = DataProvider.generateSingleDigit(1, 2)
                    maxLength = 3
                }
            }
            val firstQue: String = DataProvider.generateSingleDigit(min, max).toString()
            var answer: String = firstQue
            var mainQuestion: String = answer
            var isMinusDone = false
            var queIndex = 0
            for (ii in 0 until totalQuestion) {
                if (answer.length != maxLength) {
                    for (i in 0 until (maxLength - answer.length)) {
                        answer = "0$answer"
                    }
                }
                val questionLength = when (examLevel) {
                    AppConstants.ExamType.exam_Level_Beginner -> {
                        DataProvider.generateSingleDigit(1, maxLength)
                    }
                    AppConstants.ExamType.exam_Level_Intermediate -> {
                        DataProvider.generateSingleDigit(2, maxLength)
                    }
                    else -> {
                        DataProvider.generateSingleDigit(1, maxLength)
                    }
                }

                var newQuestion = ""
                if (questionLength != maxLength) {
                    for (i in 0 until (maxLength - questionLength)) {
                        newQuestion += "0"
                    }
                }

                if (totalQuestion == 1) {
                    for (i in newQuestion.length until maxLength) {
                        val list = AbacusProvider.numberListForSubtraction(answer[i].toString().toInt())
                        newQuestion += list.first()
                    }
                    mainQuestion = mainQuestion +"-"+newQuestion.toInt().toString()
                } else {
                    val sign = if (ii == (totalQuestion - 1) && !isMinusDone) {
                        1
                    } else {
                        DataProvider.generateSingleDigit(0, 1)
                    }

                    for (i in newQuestion.length until maxLength) {
                        val list = if (sign == 1) { // minus
                            AbacusProvider.numberListForSubtraction(answer[i].toString().toInt())
                        } else {
                            AbacusProvider.numberListForAddition(answer[i].toString().toInt())
                        }
                        newQuestion += list.first()
                    }
                    var isAdd = false
                    var signSymbol = "+"
                    if (sign == 1) { // minus
                        if (ii == 0) {
                            isMinusDone = true
                            isAdd = true
                            queIndex++
                            signSymbol = "-"
                        }else if (newQuestion.toInt() != 0) {
                            isMinusDone = true
                            isAdd = true
                            queIndex++
                            signSymbol = "-"
                        }

                    } else {
                        signSymbol = "+"
                        if (newQuestion.toInt() != 0) {
                            isAdd = true
                            queIndex++
                        }
                    }
                    if (isAdd) {
                        mainQuestion = mainQuestion+signSymbol+newQuestion.toInt().toString()
                        answer = if (signSymbol == "+"){
                            (answer.toInt() + newQuestion.toInt()).toString()
                        }else{
                            (answer.toInt() - newQuestion.toInt()).toString()
                        }
                    }
                }
            }

            val resultObject = Calculator().getResult(mainQuestion,mainQuestion)
            val correctAns = CommonUtils.removeTrailingZero(resultObject)
            return if (firstQue == correctAns){
                BeginnerExamPaper(BeginnerExamQuestionType.Subtractions,mainQuestion,"",null, isAbacusQuestion = false)
            }else if (examLevel == AppConstants.ExamType.exam_Level_Beginner) {
                val listDataObjects = getDataObjectsList()
                if (mainQuestion.contains("-")){
                    val list = mainQuestion.split("-")
                    if (list[0].toInt() < 10 && list[1].toInt() < 10){
                        BeginnerExamPaper(BeginnerExamQuestionType.Subtractions,list[0],list[1],listDataObjects.first(), isAbacusQuestion = false)
                    }
                    // TODO comment abacus question
//                    else if (DataProvider.generateSingleDigit(0, 1) == 1){
//                        BeginnerExamPaper(BeginnerExamQuestionType.Subtractions,list[0],list[1],null, isAbacusQuestion = true)
//                    }
                    else{
                        BeginnerExamPaper(BeginnerExamQuestionType.Subtractions,mainQuestion,"",null, isAbacusQuestion = false)
                    }
                }else if (DataProvider.generateSingleDigit(0, 1) == 1){
                    BeginnerExamPaper(BeginnerExamQuestionType.Subtractions,mainQuestion,"",null, isAbacusQuestion = false)
                }else{
                    BeginnerExamPaper(BeginnerExamQuestionType.Subtractions,mainQuestion,"",null, isAbacusQuestion = false)
                }
            }else{
                BeginnerExamPaper(BeginnerExamQuestionType.Subtractions,mainQuestion,"",null, isAbacusQuestion = false)
            }
        }

    }
    private fun generateExamMultiplicationDivision(examLevel : String, isDivision : Boolean = false) : BeginnerExamPaper{
        var que1 : Long = 1
        var que2 : Long = 9
        when (examLevel) {
            AppConstants.ExamType.exam_Level_Beginner -> {
                if (isDivision){
                    que1 = DataProvider.generateSingleDigit(2, 9).toLong()
                    que2 = DataProvider.generateSingleDigit(2, 9).toLong()
                }else{
                    que1 = DataProvider.generateSingleDigit(1, 9).toLong()
                    que2 = DataProvider.generateSingleDigit(1, 9).toLong()
                }
            }
            AppConstants.ExamType.exam_Level_Intermediate -> {
                if (DataProvider.generateSingleDigit(0, 1) == 1){
                    que1 = DataProvider.generateSingleDigit(11, 999).toLong()
                    que2 = DataProvider.generateSingleDigit(2, 9).toLong()
                }else{
                    if (isDivision){
                        que1 = DataProvider.generateSingleDigit(11, 9999).toLong()
                        que2 = DataProvider.generateSingleDigit(2, 9).toLong()
                    }else{
                        que1 = DataProvider.generateSingleDigit(11, 99).toLong()
                        que2 = if (DataProvider.generateSingleDigit(0, 1) == 1){
                            DataProvider.generateSingleDigit(1, 99).toLong()
                        }else{
                            DataProvider.generateSingleDigit(2, 9).toLong()
                        }
                    }


                }
            }
            AppConstants.ExamType.exam_Level_Expert -> {
                if (isDivision){
                    que1 = DataProvider.generateSingleDigit(11, 9999).toLong()
                    que2 = DataProvider.generateSingleDigit(2, 99).toLong()
                }else{
                    que1 = if (DataProvider.generateSingleDigit(0, 1) == 1){
                        DataProvider.generateSingleDigit(100, 9999).toLong()
                    }else{
                        DataProvider.generateSingleDigit(10, 999).toLong()
                    }
                    que2 = if (DataProvider.generateSingleDigit(0, 1) == 1){
                        DataProvider.generateSingleDigit(100, 9999).toLong()
                    }else{
                        DataProvider.generateSingleDigit(10, 999).toLong()
                    }
                }

            }
        }
        var type = BeginnerExamQuestionType.Multiplication
        val que = if (isDivision){
            type = BeginnerExamQuestionType.Division
            val answer : Long = que1 * que2
            if (que1 > que2){"$answer/${que2}"}else{"${answer}/${que1}"}
        }else{if (que1 > que2){"${que1}x${que2}"}else{"${que2}x${que1}"}}
        return BeginnerExamPaper(type,que,"",null, isAbacusQuestion = false)
    }

    fun generateBeginnerExamPaper(context: Context, examLevel : String) : List<BeginnerExamPaper>{
        val examList: MutableList<BeginnerExamPaper> = arrayListOf()
        var listDataObjects = getDataObjectsList()
        val pref = AppPreferencesHelper(context, AppConstants.PREF_NAME)
        // exam completed count level wise
        val previousTotalExamCount = pref.getCustomParamInt(AppConstants.extras_Comman.examGivenCount + examLevel,0)
        val listCounter: MutableList<Int> = arrayListOf()
        var totalQuestions = 10
        var countQuestions = 6
        var endNumber = 6
        val begginerExamLevel1 = 3
        val begginerExamLevel2 = 6
        val begginerExamLevel3 = 10
        val begginerExamLevel4 = 15

        if (previousTotalExamCount < begginerExamLevel1){ // 1st 3 exam
            endNumber = 6
            countQuestions = 6
        }else if (previousTotalExamCount < begginerExamLevel2){ // next 4,5,6 exam
            endNumber = 8
            countQuestions = 6
        }else if (previousTotalExamCount < begginerExamLevel3){ // next 7,8,9,10 exam
            totalQuestions = 15
            endNumber = 10
            countQuestions = 8
        }else if (previousTotalExamCount < begginerExamLevel4){ // next 11 to 15 exam
            totalQuestions = 15
            endNumber = 10
            countQuestions = 7
        }else{
            totalQuestions = 20
            endNumber = 15
            countQuestions = 6
        }


        listCounter.clear()
        for (i in 1..endNumber){ // end number = 5 then counter que generate from 1 to 5
            listCounter.add(i)
        }
        for (i in 1..countQuestions){
            listCounter.shuffle()
            listCounter.shuffle()
            if (listDataObjects.isEmpty()){
                listDataObjects = getDataObjectsList()
            }
            examList.add(BeginnerExamPaper(BeginnerExamQuestionType.Count,listCounter.first().toString(),"",listDataObjects.first()))
            listDataObjects.removeAt(0)
        }

        // additions questions
        if (previousTotalExamCount < begginerExamLevel1){ // 1st 3 exam
            endNumber = 3 // additions end number
            countQuestions = (totalQuestions - 1) - examList.size // addition question counts, 1 questions for subtraction
        }else if (previousTotalExamCount < begginerExamLevel2){ // next 4,5,6 exam
            endNumber = 4
            countQuestions = (totalQuestions - 1) - examList.size
        }else if (previousTotalExamCount < begginerExamLevel3){ // next 7,8,9,10 exam
            endNumber = 5
            countQuestions = (totalQuestions - 2) - examList.size
        }else if (previousTotalExamCount < begginerExamLevel4){ // next 11 to 15 exam
            endNumber = 9
            countQuestions = (totalQuestions - 3) - examList.size
        }else{
            endNumber = 10
            countQuestions = (totalQuestions - examList.size)/2
        }

        for (i in 1..countQuestions){
            listCounter.clear()
            for (j in 1..endNumber){
                listCounter.add(j)
            }
            listCounter.shuffle()
            listCounter.shuffle()
            val number1 = listCounter.first()
            listCounter.clear()
            if (previousTotalExamCount in begginerExamLevel3 until begginerExamLevel4){
                if (number1 > 6){
                    val tempEndNumber = 10 - number1
                    for (k in 1..tempEndNumber){
                        listCounter.add(k)
                    }
                }else{
                    for (k in 1..endNumber){
                        listCounter.add(k)
                    }
                }
            }else{
                for (k in 1..endNumber){
                    listCounter.add(k)
                }
            }

            listCounter.shuffle()
            listCounter.shuffle()
            val number2 = listCounter.first()

            if (listDataObjects.isEmpty()){
                listDataObjects = getDataObjectsList()
            }
            examList.add(BeginnerExamPaper(BeginnerExamQuestionType.Additions,number1.toString(),number2.toString(),listDataObjects.first()))
            listDataObjects.removeAt(0)
        }
        // subtraction questions
        if (previousTotalExamCount < begginerExamLevel1){ // 1st 3 exam
            endNumber = 3 // subtraction end number
            countQuestions = totalQuestions - examList.size // subtraction question counts
        }else if (previousTotalExamCount < begginerExamLevel2){ // next 4,5,6 exam
            endNumber = 5
            countQuestions = totalQuestions - examList.size
        }else if (previousTotalExamCount < begginerExamLevel3){ // next 7,8,9,10 exam
            endNumber = 7
            countQuestions = totalQuestions - examList.size
        }else if (previousTotalExamCount < begginerExamLevel4){ // next 11 to 15 exam
            endNumber = 7
            countQuestions = totalQuestions - examList.size
        }else{
            endNumber = 10
            countQuestions = totalQuestions - examList.size
        }
        for (i in 1..countQuestions){
            listCounter.clear()
            for (j in 1..endNumber){
                listCounter.add(j)
            }
            listCounter.shuffle()
            listCounter.shuffle()
            val number1 = listCounter.first()
            listCounter.clear()
            for (k in 1..number1){
                listCounter.add(k)
            }
            listCounter.shuffle()
            listCounter.shuffle()
            val number2 = listCounter.first()

            if (listDataObjects.isEmpty()){
                listDataObjects = getDataObjectsList()
            }
            examList.add(BeginnerExamPaper(BeginnerExamQuestionType.Subtractions,number1.toString(),number2.toString(),listDataObjects.first()))
            listDataObjects.removeAt(0)
        }


        if (previousTotalExamCount > 6){
            (examList as ArrayList<BeginnerExamPaper>).shuffle()
        }
        // TODO comment abacus question
//        examList.map {
//            val index = DataProvider.generateIndex(3)
//            if (index == 1){
//                it.isAbacusQuestion = true
//            }
//        }
        return examList
    }

     fun findDataObjects(objectName : String?): ImagesDataObjects?{
        val listDataObjects = addDataObjectsList()
        listDataObjects.find { it.name.equals(objectName,true) }?.also {
            return it
        }
        return null
    }
    private fun getDataObjectsList(): MutableList<ImagesDataObjects>{
        val listDataObjects = addDataObjectsList()
        listDataObjects.shuffle()
        listDataObjects.shuffle()
        return listDataObjects
    }
    private fun addDataObjectsList(): MutableList<ImagesDataObjects>{
        val listDataObjects: MutableList<ImagesDataObjects> = arrayListOf()
        with(listDataObjects){
            add(ImagesDataObjects(DataObjectsType.Objects,"Airplane","objects_airplane"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Ball","objects_ball"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Balloon","objects_balloon"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Bus","objects_bus"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Cake","objects_cake"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Cap","objects_cap"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Car","objects_car"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Chair","objects_chair"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Crown","objects_crown"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Guitar","objects_guitar"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Hammer","objects_hammer"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Hat","objects_hat"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Helicopter","objects_helicopter"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Ice Cream","objects_ice_cream"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Jeep","objects_jeep"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Kite","objects_kite"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Lock","objects_lock"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Mirror","objects_mirror"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Parachute","objects_parachute"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Pencil","objects_pencil"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Radio","objects_radio"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Robot","objects_robot"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Table","objects_table"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Teddy Bear","objects_teddy_bear"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Train","objects_train"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Umbrella","objects_umbrella"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Vas","objects_vas"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Watch","objects_watch"))
            add(ImagesDataObjects(DataObjectsType.Objects,"Wheel","objects_wheel"))

            add(ImagesDataObjects(DataObjectsType.Animal,"Ant","animal_ant"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Bee","animal_bee"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Butterfly","animal_butterfly"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Camel","animal_camel"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Cat","animal_cat"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Cow","animal_cow"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Deer","animal_deer"))
            add(ImagesDataObjects(DataObjectsType.Animal,"dinosaur","animal_dinosaur"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Dog","animal_dog"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Duck","animal_duck"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Elephant","animal_elephant"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Fox","animal_fox"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Frog","animal_frog"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Giraffe","animal_giraffe"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Goat","animal_goat"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Horse","animal_horse"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Kangaroo","animal_kangaroo"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Ladybug","animal_ladybug"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Lion","animal_lion"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Monkey","animal_monkey"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Pig","animal_pig"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Rabbit","animal_rabbit"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Rhinoceros","animal_rhinoceros"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Sheep","animal_sheep"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Snake","animal_snake"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Tiger","animal_tiger"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Turtle","animal_turtle"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Unicorn","animal_unicorn"))
            add(ImagesDataObjects(DataObjectsType.Animal,"Zebra","animal_zebra"))

            add(ImagesDataObjects(DataObjectsType.Bird,"Eagle","bird_eagle"))
            add(ImagesDataObjects(DataObjectsType.Bird,"Hen","bird_hen"))
            add(ImagesDataObjects(DataObjectsType.Bird,"Ostrich","bird_ostrich"))
            add(ImagesDataObjects(DataObjectsType.Bird,"Owl","bird_owl"))
            add(ImagesDataObjects(DataObjectsType.Bird,"Parrot","bird_parrot"))
            add(ImagesDataObjects(DataObjectsType.Bird,"Quail","bird_quail"))
            add(ImagesDataObjects(DataObjectsType.Bird,"Vulture","bird_vulture"))
            add(ImagesDataObjects(DataObjectsType.Bird,"Wood Pecker","bird_wood_pecker"))

            add(ImagesDataObjects(DataObjectsType.Fruit,"Apple","fruit_apple"))
            add(ImagesDataObjects(DataObjectsType.Fruit,"Banana","fruit_banana"))
            add(ImagesDataObjects(DataObjectsType.Fruit,"Grapes","fruit_grapes"))
            add(ImagesDataObjects(DataObjectsType.Fruit,"Kiwi","fruit_kiwi"))
            add(ImagesDataObjects(DataObjectsType.Fruit,"Mango","fruit_mango"))
            add(ImagesDataObjects(DataObjectsType.Fruit,"Orange","fruit_orange"))
            add(ImagesDataObjects(DataObjectsType.Fruit,"Pear","fruit_pear"))
            add(ImagesDataObjects(DataObjectsType.Fruit,"Pineapple","fruit_pineapple"))
            add(ImagesDataObjects(DataObjectsType.Fruit,"Watermelon","fruit_watermelon"))

            add(ImagesDataObjects(DataObjectsType.SeaAnimal,"Dolphin","sea_animal_dolphin"))
            add(ImagesDataObjects(DataObjectsType.SeaAnimal,"Fish","sea_animal_fish"))
            add(ImagesDataObjects(DataObjectsType.SeaAnimal,"Jelly Fish","sea_animal_jellyfish"))
            add(ImagesDataObjects(DataObjectsType.SeaAnimal,"Octopus","sea_animal_octopus"))
            add(ImagesDataObjects(DataObjectsType.SeaAnimal,"Penguin","sea_animal_penguin"))

            add(ImagesDataObjects(DataObjectsType.Shape,"Circle","shape_circle"))
            add(ImagesDataObjects(DataObjectsType.Shape,"Cube","shape_cube"))
            add(ImagesDataObjects(DataObjectsType.Shape,"Diamond","shape_diamond"))
            add(ImagesDataObjects(DataObjectsType.Shape,"Heart","shape_heart"))
            add(ImagesDataObjects(DataObjectsType.Shape,"Square","shape_square"))
            add(ImagesDataObjects(DataObjectsType.Shape,"Star","shape_star"))
            add(ImagesDataObjects(DataObjectsType.Shape,"Triangle","shape_triangle"))

            add(ImagesDataObjects(DataObjectsType.Other,"Boy","other_doctor"))
            add(ImagesDataObjects(DataObjectsType.Other,"Christmas Tree","other_christmas_tree"))
            add(ImagesDataObjects(DataObjectsType.Other,"Doctor","other_doctor"))
            add(ImagesDataObjects(DataObjectsType.Other,"Ear","other_ear"))
            add(ImagesDataObjects(DataObjectsType.Other,"Earth","other_earth"))
            add(ImagesDataObjects(DataObjectsType.Other,"Fairy","other_fairy"))
            add(ImagesDataObjects(DataObjectsType.Other,"Girl","other_girl"))
            add(ImagesDataObjects(DataObjectsType.Other,"Joker","other_joker"))
            add(ImagesDataObjects(DataObjectsType.Other,"King","other_king"))
            add(ImagesDataObjects(DataObjectsType.Other,"Leaf","other_leaf"))
            add(ImagesDataObjects(DataObjectsType.Other,"Lotus","other_lotus"))
            add(ImagesDataObjects(DataObjectsType.Other,"Moon","other_moon"))
            add(ImagesDataObjects(DataObjectsType.Other,"Nose","other_nose"))
            add(ImagesDataObjects(DataObjectsType.Other,"Nurse","other_nurse"))
            add(ImagesDataObjects(DataObjectsType.Other,"Plant","other_plant"))
            add(ImagesDataObjects(DataObjectsType.Other,"Queen","other_queen"))
            add(ImagesDataObjects(DataObjectsType.Other,"Rainbow","other_rainbow"))
            add(ImagesDataObjects(DataObjectsType.Other,"Rose","other_rose"))
            add(ImagesDataObjects(DataObjectsType.Other,"Sun","other_sun"))
            add(ImagesDataObjects(DataObjectsType.Other,"Tree","other_tree"))
            add(ImagesDataObjects(DataObjectsType.Other,"Volcano","other_volcano"))
        }
        return listDataObjects
    }

}