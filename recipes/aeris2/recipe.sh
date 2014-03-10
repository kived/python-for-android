#!/bin/bash

VERSION_aeris2=
#URL_aeris2=https://github.com/kivy/kivy/zipball/stable/kivy-stable.zip
P4A_aeris2_DIR=`dirname $0`/../../../aeris2-android/src/aeris2/
DEPS_aeris2=(kivy PyMySQL pytz)
MD5_aeris2=
#BUILD_aeris2=$BUILD_PATH/aeris2/$(get_directory $URL_aeris2)
BUILD_aeris2=$BUILD_PATH/aeris2/aeris2
RECIPE_aeris2=$RECIPES_PATH/aeris2

function prebuild_aeris2() {
	true
}

function build_aeris2() {
	if [ -d "$BUILD_PATH/python-install/lib/python2.7/site-packages/aeris2" ]; then
		rm -rf "$BUILD_PATH/python-install/lib/python2.7/site-packages/aeris2"
	fi
	
	cd $BUILD_aeris2

	push_arm

	export LDFLAGS="$LDFLAGS -L$LIBS_PATH"
	export LDSHARED="$LIBLINK"
	
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
	try find . -iname '*.pyx' -exec cython {} \;
	try $BUILD_PATH/python-install/bin/python.host setup.py build_ext -v
	try find build/lib.* -name "*.o" -exec $STRIP {} \;
	try $BUILD_PATH/python-install/bin/python.host setup.py install -O2
	
	try rm -rf $BUILD_PATH/python-install/lib/python*/site-packages/aeris2-*.egg

	#try rm -rf $BUILD_PATH/python-install/lib/python*/site-packages/kivy/tools

	unset LDSHARED
	pop_arm
}

function postbuild_aeris2() {
	true
}
