package com.hyman.common.util.qiyuesuosdk.lang;

import java.util.Collection;

public class CollectionUtils {

	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}
	
	public static boolean isNotEmpty(Collection<?> collection) {
		return collection != null && !collection.isEmpty();
	}
}
