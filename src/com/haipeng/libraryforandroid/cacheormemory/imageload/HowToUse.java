package com.haipeng.libraryforandroid.cacheormemory.imageload;

import android.content.Context;
import android.widget.ImageView;

import com.haipeng.libraryforandroid.R;



public class HowToUse {

	private ImageFetcher imgFetcher = null;
	private Context mContext;
	
	private ImageView mImageView;
	String  imagePath;
	private void initImgCache() {
		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(
				mContext, "detail");
		cacheParams.setMemCacheSizePercent(mContext, 0.4f);
		imgFetcher = new ImageFetcher(mContext, 500);
		imgFetcher.addImageCache(cacheParams);
		imgFetcher.setImageFadeIn(false);
		imgFetcher.setLoadingImage(R.drawable.icon_default);
	}
	
	private void ExecuteUse(){
		initImgCache();
		imgFetcher.loadImage(imagePath, mImageView, true);
	}
}
