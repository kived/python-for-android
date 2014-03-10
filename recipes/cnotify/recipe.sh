#!/bin/bash

VERSION_cnotify=
P4A_cnotify_DIR=`dirname $0`/../../../aeris2-android/src/py-cnotify/
DEPS_cnotify=(python)
MD5_cnotify=
BUILD_cnotify=$BUILD_PATH/cnotify/cnotify
RECIPE_cnotify=$RECIPES_PATH/cnotify

function prebuild_cnotify() {
	true
}

function build_cnotify() {
	if [ -d "$BUILD_PATH/python-install/lib/python2.7/site-packages/cnotify" ]; then
		rm -rf "$BUILD_PATH/python-install/lib/python2.7/site-packages/cnotify"
	fi
	
	cd $BUILD_cnotify

	push_arm

	export LDFLAGS="$LDFLAGS -L$LIBS_PATH"
	export LDSHARED="$LIBLINK"
	#export LIBLINK_NAMESPACE="cnotify"
	
	[ -d "/tmp/build-android-python" ] && rm -rf /tmp/build-android-python
	mkdir /tmp/build-android-python
	cp $BUILD_hostpython/build/lib.linux-x86_64-2.7/_io.so /tmp/build-android-python
	export PYTHONPATH=/tmp/build-android-python
	
	# fake try to be able to cythonize generated files
	$BUILD_PATH/python-install/bin/python.host setup.py build_ext
	echo '-----------------------------'
	echo 'try to cython:'
	pwd
	find . -iname '*.pyx'
	echo '-----------------------------'
	make clean
	try sh -c "find . -iname '*.pyx' | xargs cython -w $BUILD_cnotify"
	try $BUILD_PATH/python-install/bin/python.host setup.py build_ext -v
	try find build/lib.* -name "*.o" -exec $STRIP {} \;
	try $BUILD_PATH/python-install/bin/python.host setup.py install -O2
	
	try rm -rf $BUILD_PATH/python-install/lib/python*/site-packages/cnotify-*.egg

	#try rm -rf $BUILD_PATH/python-install/lib/python*/site-packages/kivy/tools

	unset LDSHARED
	#unset LIBLINK_NAMESPACE
	
	pop_arm
}

function postbuild_cnotify() {
	true
}
