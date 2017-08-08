
# android-utils 

[![](https://jitpack.io/v/shixinzhang/android-utils.svg)](https://jitpack.io/#shixinzhang/android-utils)

-----------

提取出单独的工具类，避免到处复制。

## 如何使用：

1.根项目 ``build.gradle``：

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

```

2.当前项目 ``build.gradle`` 中：

```
	dependencies {
	        compile 'com.github.shixinzhang:android-utils:1.0.2'
	}
```