#!/bin/bash

VERSION_zope_component=3.8.0
URL_zope_component=http://pypi.python.org/packages/source/z/zope.component/zope.component-$VERSION_zope_component.tar.gz
DEPS_zope_component=(python)
MD5_zope_component=c4756e4321e373fa8823b67fe38f3702
BUILD_zope_component=$BUILD_PATH/zope_component/$(get_directory $URL_zope_component)
RECIPE_zope_component=$RECIPES_PATH/zope_component

function prebuild_zope_component() {
	true
}

function build_zope_component() {

	if [ -d "$BUILD_PATH/python-install/lib/python2.7/site-packages/zope/component" ]; then
		return
	fi

	cd $BUILD_zope_component

	push_arm

	export LDFLAGS="$LDFLAGS -L$LIBS_PATH"
	export LDSHARED="$LIBLINK"
	export PYTHONPATH=$BUILD_hostpython/Lib/site-packages

        try $BUILD_hostpython/hostpython setup.py install -O2 --root=$BUILD_PATH/python-install --install-lib=lib/python2.7/site-packages

	try rm -rf $BUILD_PATH/python-install/lib/python*/site-packages/zope/component/test*
	try rm -rf $BUILD_PATH/python-install/lib/python*/site-packages/zope/component/*.txt

	unset LDSHARED

	pop_arm
}

function postbuild_zope_component() {
	true
}

