#!/bin/bash

VERSION_PyMySQL=0.6.2
URL_PyMySQL=https://pypi.python.org/packages/source/P/PyMySQL/PyMySQL-${VERSION_PyMySQL}.tar.gz
DEPS_PyMySQL=(setuptools sqlalchemy)
MD5_PyMySQL=
BUILD_PyMySQL=$BUILD_PATH/PyMySQL/$(get_directory $URL_PyMySQL)
RECIPE_PyMySQL=$RECIPES_PATH/PyMySQL

function prebuild_PyMySQL() {
	true
}

function build_PyMySQL() {

	if [ -d "$BUILD_PATH/python-install/lib/python2.7/site-packages/PyMySQL" ]; then
		return
	fi

	cd $BUILD_PyMySQL
    
    # check marker in our source build
	if [ -f .patched ]; then
		# no patch needed
		return
	fi

	try patch -p1 < $RECIPE_PyMySQL/patches/fix_setup.patch
	
	push_arm
	export LDFLAGS="$LDFLAGS -L$LIBS_PATH"
	export LDSHARED="$LIBLINK"

	# Needed for some builds - if you get errors about _io.so, uncomment this
	#export PYTHONPATH=$BUILD_hostpython/Lib/site-packages:$BUILD_hostpython/build/lib.linux-x86_64-2.7

	try $BUILD_hostpython/hostpython setup.py install -O2 --root=$BUILD_PATH/python-install --install-lib=lib/python2.7/site-packages

	# If you need to remove any files:
	#try rm -rf $BUILD_PATH/python-install/lib/python*/site-packages/PyMySQL/<file names...>

	unset LDSHARED

	pop_arm
	
	touch .patched
}

function postbuild_PyMySQL() {
	true
}

