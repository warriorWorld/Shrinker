package com.harbinger.shrinker.filedetecter

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.harbinger.shrinker.utils.FileSpider
import com.harbinger.shrinker.utils.VideoUtil
import io.reactivex.rxjava3.core.Observable
import java.io.File
import java.util.*

/**
 * Created by acorn on 2023/8/30.
 */
class ViewModelImageDetecter {

    fun findImagesAccordingly(
        context: Context,
        fileType: FileType,
        fileSize: Long
    ): Observable<ModelImage> {
        return Observable.create {
            var c: Cursor? = null
            try {
                c = context.applicationContext.contentResolver.query(
                    MediaStore.Files.getContentUri("external"),
                    arrayOf("_id", MediaStore.Files.FileColumns.DATA),
                    null,
                    null,
                    null
                )
                val dataindex = c?.getColumnIndex(MediaStore.Files.FileColumns.DATA) ?: 0
                while (c?.moveToNext() == true) {
                    val path = c.getString(dataindex)
                    when (fileType) {
                        FileType.IMAGE -> {
                            if (FileSpider.getInstance().isImg(path)) {
                                val file = File(path)
                                val size = FileSpider.getInstance().getFileSize(file)
                                if (size > fileSize) {
                                    it.onNext(ModelImage(file, size))
                                }
                            }
                        }
                        FileType.GIF -> {
                            if (FileSpider.getInstance().isGif(path)) {
                                val file = File(path)
                                val size = FileSpider.getInstance().getFileSize(file)
                                if (size > fileSize) {
                                    it.onNext(ModelImage(file, size))
                                }
                            }
                        }
                        FileType.VIDEO -> {

                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                it.onError(e)
            } finally {
                c?.close()
                it.onComplete()
            }
        }
    }

    fun findVideosAccordingly(
        context: Context,
        fileSize: Long
    ): Observable<ModelVideo> {
        return Observable.create {
            var c: Cursor? = null
            try {
                // String[] mediaColumns = { "_id", "_data", "_display_name",
                // "_size", "date_modified", "duration", "resolution" };
                c = context.applicationContext.contentResolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    MediaStore.Video.Media.DEFAULT_SORT_ORDER
                )
                while (c?.moveToNext() == true) {
                    val path =
                        c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)) // 路径
                    val file = File(path)
                    if (!file.exists()) {
                        continue
                    }
                    val id = c.getInt(c.getColumnIndexOrThrow(MediaStore.Video.Media._ID)) // 视频的id
                    val name =
                        c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)) // 视频名称
                    val resolution =
                        c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION)) //分辨率
                    val size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)) // 大小
                    val duration =
                        c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)) // 时长
                    val date =
                        c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)) //修改时间
                    val thumbnial: Bitmap = VideoUtil.getVideoThumbnail(context, Uri.parse(path))
                    it.onNext(ModelVideo(file, thumbnial, name, (duration / 1000f).toInt(), size))
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                it.onError(e)
            } finally {
                c?.close()
                it.onComplete()
            }
        }
    }
}