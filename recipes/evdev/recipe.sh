#!/bin/bash

VERSION_evdev=v0.4.7
URL_evdev=https://github.com/gvalkov/python-evdev/archive/${VERSION_evdev}.zip
DEPS_evdev=(python)
MD5_evdev=
BUILD_evdev=$BUILD_PATH/evdev/$(get_directory $URL_evdev)
RECIPE_evdev=$RECIPES_PATH/evdev

function prebuild_evdev() {
	cd $BUILD_evdev

	if [ -f .patched ]; then
		return
	fi

	try patch -p1 < $RECIPE_evdev/patches/evcnt.patch
	try patch -p1 < $RECIPE_evdev/patches/keycnt.patch
	try patch -p1 < $RECIPE_evdev/patches/remove-uinput.patch
	try patch -p1 < $RECIPE_evdev/patches/include-dir.patch
	try patch -p1 < $RECIPE_evdev/patches/evdev-permissions.patch
	#try patch -p1 < $RECIPE_evdev/patches/test.patch

	try sed -i 's,##INCLUDE_DIR##,'$NDKPLATFORM',' setup.py

	touch .patched
}

function shouldbuild_evdev() {
	if [ -d "$SITEPACKAGES_PATH/evdev" ]; then
		DO_BUILD=0
	fi
}

function build_evdev() {
	cd $BUILD_evdev

	push_arm

	export LDFLAGS="$LDFLAGS -L$LIBS_PATH"
	export LDSHARED="$LIBLINK"

	try $HOSTPYTHON setup.py build -v
	try find build/lib.* -name "*.o" -exec $STRIP {} \;
	try $HOSTPYTHON setup.py install -O2

	unset LDSHARED
	pop_arm
}

function postbuild_evdev() {
	true
}
