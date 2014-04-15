\#!/bin/bash

VERSION_sqlalchemy=${VERSION_sqlalchemy:-0.8.4}
URL_sqlalchemy=https://pypi.python.org/packages/source/S/SQLAlchemy/SQLAlchemy-${VERSION_sqlalchemy}.tar.gz
DEPS_sqlalchemy=(python)
MD5_sqlalchemy=
BUILD_sqlalchemy=$BUILD_PATH/sqlalchemy/$(get_directory $URL_sqlalchemy)
RECIPE_sqlalchemy=$RECIPES_PATH/sqlalchemy

function prebuild_sqlalchemy() {
	true
}

function shouldbuild_sqlalchemy() {
	if [ -d "$SITEPACKAGES_PATH/sqlalchemy" ]; then
		DO_BUILD=0
	fi
}

function build_sqlalchemy() {
	cd $BUILD_sqlalchemy
	
	#cp $RECIPE_sqlalchemy/threadlocal.py $BUILD_sqlalchemy/lib/sqlalchemy/engine/threadlocal.py

	push_arm
	export LDFLAGS="$LDFLAGS -L$LIBS_PATH"
	export LDSHARED="$LIBLINK"

	export PYTHONPATH=$BUILD_hostpython/build/lib.linux-x86_64-2.7

	rm -rf $BUILD_sqlalchemy/build/

	sed -i 's/\(setup(name="SQLAlchemy",[[:space:]]*$\)/\1 zip_safe=False,/' setup.py

    $HOSTPYTHON setup.py build_ext
    try $HOSTPYTHON setup.py build_ext -v
    try find build/lib.* -name "*.o" -exec $STRIP {} \;
    try $HOSTPYTHON setup.py install -O2
    
    #try rm -rf $BUILD_PATH/python-install/lib/python*/site-packages/SQLAlchemy-*.egg

	unset LDSHARED

	pop_arm
}

function postbuild_sqlalchemy() {
	true
}

