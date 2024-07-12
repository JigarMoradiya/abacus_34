package com.jigar.me.data.model

import com.google.gson.annotations.SerializedName

data class AdditionSubtractionAbacus(
    @SerializedName("id") var id: String = "",
    @SerializedName("q0") var q0: String = "",
    @SerializedName("q1") var q1: String = "",
    @SerializedName("q2") var q2: String? = null,
    @SerializedName("q3") var q3: String? = null,
    @SerializedName("q4") var q4: String? = null,
    @SerializedName("q5") var q5: String? = null,
    @SerializedName("h1") var h1: String? = null,
    @SerializedName("h2") var h2: String? = null,
    @SerializedName("h3") var h3: String? = null,
    @SerializedName("h4") var h4: String? = null,
    @SerializedName("h5") var h5: String? = null,
    @SerializedName("s1") var s1: String = "",
    @SerializedName("s2") var s2: String? = null,
    @SerializedName("s3") var s3: String? = null,
    @SerializedName("s4") var s4: String? = null,
    @SerializedName("s5") var s5: String? = null
//    @SerializedName("Ans") var ans: String = "",
//    @SerializedName("abacus_id") var abacusId: String = "",
    ){
    fun getQuestion() : String{
        var question = q0
        if (q1.isNotEmpty() && s1.isNotEmpty()){
            question = question+s1+q1
            if (q2?.isEmpty() == false && s2?.isEmpty() == false){
                question = question+s2+q2
                if (q3?.isEmpty() == false && s3?.isEmpty() == false){
                    question = question+s3+q3
                    if (q4?.isEmpty() == false && s4?.isEmpty() == false){
                        question = question+s4+q4
                        if (q5?.isEmpty() == false && s5?.isEmpty() == false){
                            question = question+s5+q5
                        }
                    }
                }
            }
        }
        return question
    }
//    fun getAnswer() : Int{
//        var answer = 0
//        if (s1 == "+"){
//            answer = q0.toInt() + q1.toInt()
//        }else if (s1 == "-"){
//            answer = q0.toInt() - q1.toInt()
//        }
//        if (!s2.isNullOrEmpty()){
//            if (s2 == "+"){
//                answer += (q2 ?: "0").toInt()
//            }else if (s2 == "-"){
//                answer -= (q2 ?: "0").toInt()
//            }
//
//            if (!s3.isNullOrEmpty()){
//                if (s3 == "+"){
//                    answer += (q3 ?: "0").toInt()
//                }else if (s3 == "-"){
//                    answer -= (q3 ?: "0").toInt()
//                }
//
//                if (!s4.isNullOrEmpty()){
//                    if (s4 == "+"){
//                        answer += (q4 ?: "0").toInt()
//                    }else if (s4 == "-"){
//                        answer -= (q4 ?: "0").toInt()
//                    }
//
//                    if (!s5.isNullOrEmpty()){
//                        if (s2 == "+"){
//                            answer += (q5 ?: "0").toInt()
//                        }else if (s5 == "-"){
//                            answer -= (q5 ?: "0").toInt()
//                        }
//                    }
//                }
//
//            }
//        }
//        return answer
//    }
fun getAnswer() : Double{
    var answer = 0.0
    if (s1 == "+"){
        answer = q0.toDouble() + q1.toDouble()
    }else if (s1 == "-"){
        answer = q0.toDouble() - q1.toDouble()
    }
    if (!s2.isNullOrEmpty()){
        if (s2 == "+"){
            answer += (q2 ?: "0").toDouble()
        }else if (s2 == "-"){
            answer -= (q2 ?: "0").toDouble()
        }

        if (!s3.isNullOrEmpty()){
            if (s3 == "+"){
                answer += (q3 ?: "0").toDouble()
            }else if (s3 == "-"){
                answer -= (q3 ?: "0").toDouble()
            }

            if (!s4.isNullOrEmpty()){
                if (s4 == "+"){
                    answer += (q4 ?: "0").toDouble()
                }else if (s4 == "-"){
                    answer -= (q4 ?: "0").toDouble()
                }

                if (!s5.isNullOrEmpty()){
                    if (s2 == "+"){
                        answer += (q5 ?: "0").toDouble()
                    }else if (s5 == "-"){
                        answer -= (q5 ?: "0").toDouble()
                    }
                }
            }

        }
    }
    return answer
}
}