package com.tvappstore.app.ui.presenter

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.tvappstore.app.R
import com.tvappstore.app.data.model.AppInfo

/**
 * 应用卡片 Presenter
 */
class AppCardPresenter : Presenter() {
    
    private var defaultCardImage: Drawable? = null
    
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val context = parent.context
        defaultCardImage = ContextCompat.getDrawable(context, R.drawable.ic_app_default)
        
        val cardView = ImageCardView(context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
        }
        
        cardView.mainImageView.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
        
        return ViewHolder(cardView)
    }
    
    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val app = item as AppInfo
        val cardView = viewHolder.view as ImageCardView
        
        cardView.titleText = app.name
        cardView.contentText = "${app.category ?: "应用"} • ${app.version}"
        
        // 加载图标
        if (!app.iconUrl.isNullOrEmpty()) {
            Glide.with(cardView.context)
                .load(app.iconUrl)
                .placeholder(defaultCardImage)
                .error(defaultCardImage)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(object : DrawableImageViewTarget(cardView.mainImageView) {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        super.onResourceReady(resource, transition)
                    }
                })
        } else {
            cardView.mainImageView.setImageDrawable(defaultCardImage)
        }
    }
    
    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        cardView.badgeImage = null
        cardView.mainImage = null
    }
    
    companion object {
        private const val CARD_WIDTH = 200
        private const val CARD_HEIGHT = 200
    }
}
