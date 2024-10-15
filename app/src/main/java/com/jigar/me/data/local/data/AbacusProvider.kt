package com.jigar.me.data.local.data

import com.jigar.me.data.model.AdditionSubtractionAbacus
import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.utils.Constants
import org.json.JSONObject
import java.util.Random

object AbacusProvider {
    fun getHashMapList(currentAbacus: Abacus): java.util.ArrayList<java.util.HashMap<String, String>> {
        val list_abacus = java.util.ArrayList<java.util.HashMap<String, String>>()
        val que = currentAbacus.question
        var data: java.util.HashMap<String, String> = java.util.HashMap()
        if (que.contains("*", true)) {
            val list = que.split("*")
            data[Constants.Que] = list[0]
            data[Constants.Sign] = ""
            list_abacus.add(data)
            data = java.util.HashMap()
            data[Constants.Que] = list[1]
            data[Constants.Sign] = "*"
            list_abacus.add(data)
        } else if (que.contains("/", true)) {
            val list = que.split("/")
            data[Constants.Que] = list[0]
            data[Constants.Sign] = ""
            list_abacus.add(data)
            data = java.util.HashMap()
            data[Constants.Que] = list[1]
            data[Constants.Sign] = "/"
            list_abacus.add(data)
        } else { // +, -
            val newQue = que.replace("+", "$$+").replace("-", "$$-")
            val list = newQue.split("$$")
            var position = 0
            list.map {
                data = java.util.HashMap()
                data[Constants.Que] = it.replace("+", "").replace("-", "")
                if (it.contains("+")) {
                    data[Constants.Sign] = "+"
                } else if (it.contains("-")) {
                    data[Constants.Sign] = "-"
                } else {
                    data[Constants.Sign] = ""
                }
                val hint = ""
//                val hint = if (position == 0) {
//                    ""
//                } else if (position == 1) {
//                    currentAbacus.hint1
//                } else if (position == 2) {
//                    currentAbacus.hint2
//                } else if (position == 3) {
//                    currentAbacus.hint3
//                } else if (position == 4) {
//                    currentAbacus.hint4
//                } else if (position == 5) {
//                    currentAbacus.hint5
//                } else {
//                    ""
//                }
                data[Constants.Hint] = hint ?: ""
                list_abacus.add(data)
                position += 1
            }
        }
        return list_abacus
    }
    fun numberListForAddition(number: Int): List<String> {
        val str = when (number) {
            0 -> {
                "9,3,6,2,4,7,8,1,5"
            }

            1 -> {
                "2,1,3,5,0,6,7,8"
            }

            2 -> {
                "2,1,5,6,0,7"
            }

            3 -> {
                "1,0,5,6"
            }

            4 -> {
                "5,0"
            }

            5 -> {
                "1,2,0,3,4"
            }

            6 -> {
                "3,0,1,2"
            }

            7 -> {
                "0,1,2"
            }

            8 -> {
                "1,0"
            }

            else -> {
                "0"
            }
        }
        return listOf(*str.split(",").toTypedArray()).shuffled().shuffled()
    }

    fun numberListForSubtraction(number: Int): List<String> {
        val str = when (number) {
            1 -> {
                "1,0"
            }

            2 -> {
                "1,0,2"
            }

            3 -> {
                "1,2,0,3"
            }

            4 -> {
                "4,0,1,3,2"
            }

            5 -> {
                "5,0"
            }

            6 -> {
                "1,6,0,5"
            }

            7 -> {
                "5,0,1,2,6,7"
            }

            8 -> {
                "1,0,2,6,7,8,3,5"
            }

            9 -> {
                "1,4,9,0,2,6,7,8,3,5"
            }

            else -> {
                "0"
            }
        }
        return listOf(*str.split(",").toTypedArray()).shuffled().shuffled()
    }

    fun generateAdditionDigit(
        pageId: String,
        maxLength: Int = 4,
        totalSubQuestion: Int = 4
    ): AdditionSubtractionAbacus {
        val data = AdditionSubtractionAbacus()
        var min = 1000
        var max = 9000
        val totalQuestion: Int = DataProvider.generateSingleDigit(1, totalSubQuestion)
        if (maxLength == 2) {
            min = 1
            max = 100
        }else if (maxLength == 3) {
            min = 100
            max = 1000
        }else if (maxLength == 4) {
            min = 1000
            if (pageId == "1001") {
                max = 3000
            } else {
                when (totalQuestion) {
                    4 -> {
                        max = 2500
                    }

                    3 -> {
                        max = 3000
                    }

                    2 -> {
                        max = 4000
                    }
                }
            }

        } else if (maxLength == 5) {
            max = 90000
            min = 10000
            if (pageId == "2001") {
                max = 30000
            } else {
                when (totalQuestion) {
                    4 -> {
                        max = 25000
                    }

                    3 -> {
                        max = 30000
                    }

                    2 -> {
                        max = 40000
                    }
                }
            }

        }

        val questionList: ArrayList<Int> = arrayListOf()

        var answer: String = DataProvider.generateSingleDigit(min, max).toString()
        questionList.add(answer.toInt())
        for (ii in 0 until totalQuestion) {
            if (answer.length != maxLength) {
                for (i in 0 until (maxLength - answer.length)) {
                    answer = "0$answer"
                }
            }
            val questionLength = when (pageId) {
                "1001" -> {
                    DataProvider.generateSingleDigit(2, 3)
                }
                "2001" -> {
                    DataProvider.generateSingleDigit(3, 4)
                }
                "1051","1052" -> {
                    DataProvider.generateSingleDigit(2, 3)
                }

                else -> {
                    if (maxLength == 2){
                        DataProvider.generateSingleDigit(1, maxLength)
                    }else{
                        DataProvider.generateSingleDigit(3, maxLength)
                    }

                }
            }

            var newQuestion = ""
            if (questionLength != maxLength) {
                for (i in 0 until (maxLength - questionLength)) {
                    newQuestion += "0"
                }
            }
            for (i in newQuestion.length until maxLength) {
                val list = numberListForAddition(answer[i].toString().toInt())
                newQuestion += list.first()
            }

            if (newQuestion.toInt() != 0) {
                questionList.add(newQuestion.toInt())
                answer = (answer.toInt() + newQuestion.toInt()).toString()
            }
        }

        if (questionList.size == 1) {
            return generateAdditionDigit(pageId, maxLength, totalSubQuestion)
        } else {
            if (totalSubQuestion > 1) {
                questionList.shuffle()
            }
            questionList.onEachIndexed { index, que ->
                when (index) {
                    0 -> {
                        data.q0 = que.toString()
                    }

                    1 -> {
                        data.q1 = que.toString()
                        data.s1 = "+"
                    }

                    2 -> {
                        data.q2 = que.toString()
                        data.s2 = "+"
                    }

                    3 -> {
                        data.q3 = que.toString()
                        data.s3 = "+"
                    }

                    4 -> {
                        data.q4 = que.toString()
                        data.s4 = "+"
                    }

                    5 -> {
                        data.q5 = que.toString()
                        data.s5 = "+"
                    }
                }
            }
        }


        return data
    }

    fun generateSubtractionDigit(
        pageId: String,
        maxLength: Int = 4,
        totalSubQuestion: Int = 4
    ): AdditionSubtractionAbacus {
        val data = AdditionSubtractionAbacus()
        var min = 1000
        var max = 9000

        val totalQuestion: Int = when (pageId) {
            "5001" -> {
                min = 100
                max = 400
                totalSubQuestion
            }
            "5002" -> {
                min = 100
                max = 700
                totalSubQuestion
            }
            "5003" -> {
                min = 100
                max = 900
                DataProvider.generateSingleDigit(3, totalSubQuestion)
            }
            "5004","5005" -> {
                min = 100
                max = 1000
                DataProvider.generateSingleDigit(4, totalSubQuestion)
            }
            "6001" -> {
                min = 1000
                max = 4000
                totalSubQuestion
            }
            "6002" -> {
                min = 1000
                max = 7000
                totalSubQuestion
            }
            "6003" -> {
                min = 1000
                max = 9000
                DataProvider.generateSingleDigit(3, totalSubQuestion)
            }
            "6004","6005" -> {
                min = 1000
                max = 10000
                DataProvider.generateSingleDigit(4, totalSubQuestion)
            }
            "7001" -> {
                min = 10000
                max = 30000
                totalSubQuestion
            }
            "7002" -> {
                min = 10000
                max = 60000
                totalSubQuestion
            }
            "7003" -> {
                min = 10000
                max = 85000
                DataProvider.generateSingleDigit(3, totalSubQuestion)
            }
            "7004","7005" -> {
                min = 10000
                max = 100000
                DataProvider.generateSingleDigit(4, totalSubQuestion)
            }
            else -> {
                totalSubQuestion
            }
        }

        var answer: String = DataProvider.generateSingleDigit(min, max).toString()
        data.q0 = answer

        var isMinusDone = false
        var queIndex = 0
        for (ii in 0 until totalQuestion) {
            if (answer.length != maxLength) {
                for (i in 0 until (maxLength - answer.length)) {
                    answer = "0$answer"
                }
            }
            val questionLength = when (pageId) {
                "7001","7002","7003","7004","7005" -> { // 4 digits
                    DataProvider.generateSingleDigit(3, maxLength)
                }
                "6001","6002","6003","6004","6005 " -> { // 4 digits
                    DataProvider.generateSingleDigit(3, maxLength)
                }
                else -> { // 3 digits
                    DataProvider.generateSingleDigit(2, maxLength)
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
                    val list = numberListForSubtraction(answer[i].toString().toInt())
                    newQuestion += list.first()
                }
                data.q1 = newQuestion.toInt().toString()
                data.s1 = "-"
            } else {
                val sign = if (ii == (totalQuestion - 1) && !isMinusDone) {
                    1
                } else {
                    DataProvider.generateSingleDigit(0, 1)
                }

                for (i in newQuestion.length until maxLength) {
                    val list = if (sign == 1) { // minus
                        numberListForSubtraction(answer[i].toString().toInt())
                    } else {
                        numberListForAddition(answer[i].toString().toInt())
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
                    if (queIndex == 1) {
                        data.q1 = newQuestion.toInt().toString()
                        data.s1 = signSymbol
                    } else if (queIndex == 2) {
                        data.q2 = newQuestion.toInt().toString()
                        data.s2 = signSymbol
                    } else if (queIndex == 3) {
                        data.q3 = newQuestion.toInt().toString()
                        data.s3 = signSymbol
                    } else if (queIndex == 4) {
                        data.q4 = newQuestion.toInt().toString()
                        data.s4 = signSymbol
                    } else if (queIndex == 5) {
                        data.q5 = newQuestion.toInt().toString()
                        data.s5 = signSymbol
                    }
                    answer = if (signSymbol == "+"){
                        (answer.toInt() + newQuestion.toInt()).toString()
                    }else{
                        (answer.toInt() - newQuestion.toInt()).toString()
                    }

                }
            }
        }
        return data
    }

    fun generateMultiplication(
        que2_str1: String,
        que2_type: String,
        que1_digit_type: Int
    ): ArrayList<HashMap<String, String>> {
        var que2_str = que2_str1
        val list_abacus = ArrayList<HashMap<String, String>>()
        var que2 = 0
        val que1 = if (que1_digit_type == 2) {
            val max = 100
            val min = 10
            DataProvider.generateSingleDigit(min, max)
        } else if (que1_digit_type == 3) {
            val max = 1000
            val min = 100
            DataProvider.generateSingleDigit(min, max)
        } else if (que1_digit_type == 30) {
            val max = 300
            val min = 100
            DataProvider.generateSingleDigit(min, max)
        } else if (que1_digit_type == 300) {
            val max = 700
            val min = 300
            DataProvider.generateSingleDigit(min, max)
        } else if (que1_digit_type == 4) {
            val max = 10000
            val min = 1000
            DataProvider.generateSingleDigit(min, max)
        } else if (que1_digit_type == 40) {
            val max = 3000
            val min = 1000
            DataProvider.generateSingleDigit(min, max)
        } else if (que1_digit_type == 400) {
            val max = 7000
            val min = 3000
            DataProvider.generateSingleDigit(min, max)
        } else {
            val max = 1000
            val min = 100
            DataProvider.generateSingleDigit(min, max)
        }
        var data: HashMap<String, String> = HashMap()
        data[Constants.Que] = que1.toString()
        data[Constants.Sign] = ""
        list_abacus.add(data)
        when {
            que2_str.isEmpty() -> {
                var strArray: Array<String>? = null
                when {
                    que2_type.equals("234", ignoreCase = true) -> {
                        strArray = arrayOf("2", "3", "4")
                    }

                    que2_type.equals("567", ignoreCase = true) -> {
                        strArray = arrayOf("5", "6", "7")
                    }

                    que2_type.equals("89", ignoreCase = true) -> {
                        strArray = arrayOf("8", "9")
                    }

                    que2_type.equals("1..9", ignoreCase = true) -> {
                        strArray = arrayOf("2", "3", "4", "5", "6", "7", "8", "9")
                    }

                    que2_type.equals("02", ignoreCase = true) -> {
                        strArray = arrayOf("12", "22", "32", "42", "52", "62", "72", "82", "92")
                    }

                    que2_type.equals("03", ignoreCase = true) -> {
                        strArray = arrayOf("13", "23", "33", "43", "53", "63", "73", "83", "93")
                    }

                    que2_type.equals("04", ignoreCase = true) -> {
                        strArray = arrayOf("14", "24", "34", "44", "54", "64", "74", "84", "94")
                    }

                    que2_type.equals("05", ignoreCase = true) -> {
                        strArray = arrayOf("15", "25", "35", "45", "55", "65", "75", "85", "95")
                    }

                    que2_type.equals("06", ignoreCase = true) -> {
                        strArray = arrayOf("16", "26", "36", "46", "56", "66", "76", "86", "96")
                    }

                    que2_type.equals("07", ignoreCase = true) -> {
                        strArray = arrayOf("17", "27", "37", "47", "57", "67", "77", "87", "97")
                    }

                    que2_type.equals("08", ignoreCase = true) -> {
                        strArray = arrayOf("18", "28", "38", "48", "58", "68", "78", "88", "98")
                    }

                    que2_type.equals("09", ignoreCase = true) -> {
                        strArray = arrayOf("19", "29", "39", "49", "59", "69", "79", "89", "99")
                    }
                }
                que2_str = strArray!![Random().nextInt(strArray.size)]
                que2 = que2_str.toInt()
            }

            que2_str.equals("ran2", ignoreCase = true) -> {
                val max = 100
                val min = 10
                que2 = DataProvider.generateSingleDigit(min, max)
            }

            que2_str.equals("ran2_1", ignoreCase = true) -> {
                val max = 40
                val min = 10
                que2 = DataProvider.generateSingleDigit(min, max)
            }

            que2_str.equals("ran2_2", ignoreCase = true) -> {
                val max = 70
                val min = 30
                que2 = DataProvider.generateSingleDigit(min, max)
            }

            que2_str.equals("ran3", ignoreCase = true) -> {
                val max = 1000
                val min = 100
                que2 = DataProvider.generateSingleDigit(min, max)
            }

            que2_str.equals("ran3_1", ignoreCase = true) -> {
                val max = 300
                val min = 100
                que2 = DataProvider.generateSingleDigit(min, max)
            }

            que2_str.equals("ran3_2", ignoreCase = true) -> {
                val max = 600
                val min = 200
                que2 = DataProvider.generateSingleDigit(min, max)
            }

            que2_str.equals("ran4", ignoreCase = true) -> {
                val max = 10000
                val min = 1000
                que2 = DataProvider.generateSingleDigit(min, max)
            }

            que2_str.equals("ran4_1", ignoreCase = true) -> {
                val max = 3000
                val min = 1000
                que2 = DataProvider.generateSingleDigit(min, max)
            }

            que2_str.equals("ran4_2", ignoreCase = true) -> {
                val max = 6000
                val min = 2000
                que2 = DataProvider.generateSingleDigit(min, max)
            }

            else -> {
                que2 = que2_str.toInt()
            }
        }
        data = HashMap()
        data[Constants.Que] = que2.toString()
        data[Constants.Sign] = "*"
        list_abacus.add(data)
        return list_abacus
    }


    fun generateDevide(
        que2_str1: String,
        que2_type: String,
        position: Int
    ): ArrayList<HashMap<String, String>> {
        var que2_str = que2_str1
        val list_abacus = ArrayList<HashMap<String, String>>()
        var que2: Int
        var que1 = when {
            position < 10 -> {
                val max = 111
                val min = 1
                DataProvider.generateSingleDigit(min, max)
            }

            position < 20 -> {
                val max = 500
                val min = 1
                DataProvider.generateSingleDigit(min, max)
            }

            else -> {
                val max = 1000
                val min = 1
                DataProvider.generateSingleDigit(min, max)
            }
        }
        when {
            que2_str.isEmpty() -> {
                var strArray: Array<String>? = null
                when {
                    que2_type.equals("234", ignoreCase = true) -> {
                        strArray = arrayOf("2", "3", "4")
                    }

                    que2_type.equals("567", ignoreCase = true) -> {
                        strArray = arrayOf("5", "6", "7")
                    }

                    que2_type.equals("89", ignoreCase = true) -> {
                        strArray = arrayOf("8", "9")
                    }

                    que2_type.equals("1..9", ignoreCase = true) -> {
                        strArray = arrayOf("2", "3", "4", "5", "6", "7", "8", "9")
                    }

                    que2_type.equals("00", ignoreCase = true) -> {
                        strArray = arrayOf("40", "10", "20", "30")
                    }

                    que2_type.equals("01", ignoreCase = true) -> {
                        strArray = arrayOf("41", "11", "21", "31")
                    }

                    que2_type.equals("02", ignoreCase = true) -> {
                        strArray = arrayOf("42", "12", "22", "32")
                    }

                    que2_type.equals("03", ignoreCase = true) -> {
                        strArray = arrayOf("43", "13", "23", "33")
                    }

                    que2_type.equals("04", ignoreCase = true) -> {
                        strArray = arrayOf("44", "14", "24", "34")
                    }

                    que2_type.equals("05", ignoreCase = true) -> {
                        strArray = arrayOf("45", "15", "25", "35")
                    }

                    que2_type.equals("06", ignoreCase = true) -> {
                        strArray = arrayOf("46", "16", "26", "36")
                    }

                    que2_type.equals("07", ignoreCase = true) -> {
                        strArray = arrayOf("47", "17", "27", "37")
                    }

                    que2_type.equals("08", ignoreCase = true) -> {
                        strArray = arrayOf("48", "18", "28", "38")
                    }

                    que2_type.equals("09", ignoreCase = true) -> {
                        strArray = arrayOf("49", "19", "29", "39")
                    }
                }
                que2_str = strArray!![Random().nextInt(strArray.size)]
                que2 = que2_str.toInt()
            }

            que2_str.equals("ran2", ignoreCase = true) -> {
                val max = 30
                val min = 10
                que2 = DataProvider.generateSingleDigit(min, max)
            }

            que2_str.equals("ran2_1", ignoreCase = true) -> {
                val max = 60
                val min = 10
                que2 = DataProvider.generateSingleDigit(min, max)
            }

            que2_str.equals("ran3", ignoreCase = true) -> {
                val max = 1000
                val min = 100
                que2 = DataProvider.generateSingleDigit(min, max)
            }

            else -> {
                que2 = que2_str.toInt()
            }
        }
//        que1 = 496
//        que2 = 5
        var data: HashMap<String, String> = HashMap()
        val final_que1 = que1 * que2
        data[Constants.Que] = final_que1.toString()
        data[Constants.Sign] = ""
        list_abacus.add(data)
        data = HashMap()
        data[Constants.Que] = que2.toString()
        data[Constants.Sign] = "/"
        list_abacus.add(data)
        return list_abacus
    }

}