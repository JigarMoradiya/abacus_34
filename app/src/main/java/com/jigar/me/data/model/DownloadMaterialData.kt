package com.jigar.me.data.model

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

data class DownloadMaterialData(
    @SerializedName("group_id") var groupId: String = "",
    @SerializedName("group_name") var groupName: String = "",
    @SerializedName("image_path") var imagePath: String = "",
    @SerializedName("pdf_path") var pdf_path: String = "",
    @SerializedName("images_list") var imagesList: List<ImageData> = ArrayList<ImageData>()
)

data class ImageData(
    @SerializedName("group_id") var groupId: String = "",
    @SerializedName("image_id") var imageId: String = "",
    @SerializedName("image") var image: String = "",
    @SerializedName("description") var description: String = "",
    @SerializedName("tag") var tag: String = "")