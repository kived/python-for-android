#!/bin/bash -e

NAME=$1

if [ -z "$NAME" ]; then
	echo "Please provide a name!"
	exit 1
fi

cd `dirname $0`

if [ -d "$NAME" ]; then
	echo "Recipe already exists!"
	exit 2
fi

mkdir "$NAME"

NAME1=`echo $NAME | sed 's/^\(.\).*$/\1/'`

cat mkrecipe.tpl | sed -e 's/#NAME1#/'"$NAME1"'/g' -e 's/#NAME#/'"$NAME"'/g' > "$NAME/recipe.sh"
nano "$NAME/recipe.sh"

exit 0
