#!/bin/bash

VERSION_eesentinel=
P4A_eesentinel_DIR=`dirname $0`/../../../aeris2-android/src/ee-sentinel/
DEPS_eesentinel=(twisted)
MD5_eesentinel=
BUILD_eesentinel=$BUILD_PATH/eesentinel/ee-sentinel
RECIPE_eesentinel=$RECIPES_PATH/eesentinel

function prebuild_eesentinel() {
	true
}

function build_eesentinel() {
	if [ -d "$BUILD_PATH/python-install/lib/python2.7/site-packages/ee/sentinel" ]; then
		rm -rf "$BUILD_PATH/python-install/lib/python2.7/site-packages/ee/sentinel"
	fi
	
	cd $BUILD_eesentinel

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
	
	try rm -rf $BUILD_PATH/python-install/lib/python*/site-packages/ee.sentinel.*.egg

	unset LDSHARED
	pop_arm
}

function postbuild_eesentinel() {
	true
}
