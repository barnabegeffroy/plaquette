set -e
REPO="receive_plaquette"
FILE_LOG=out.log
FILE_PDF=out.pdf
DEPLOY_REPO="https://${MY_DOC}@github.com/barnabegeffroy/${REPO}.git"
BUILT=0
function main {
	clean
	get_current_doc
	build_doc
	if [ -z "$TRAVIS_PULL_REQUEST" ]; then
	   	echo "except don't publish doc for pull requests"
    else 
		deploy
	fi  
}

function clean { 
	echo "cleaning docs"
	if [ -f "${FILE_LOG}" ]; then rm -f ${FILE_LOG}; fi 
	if [ -f "${FILE_PDF}" ]; then rm -f ${FILE_PDF}; fi 
}

function get_current_doc { 
	echo "getting latest doc"
	git clone --depth 1 $DEPLOY_REPO ../${REPO}
}

function build_doc { 
	echo "trying to build doc"
	if	java -cp "target/classes:$(cat .classpath)" "io.github.oliviercailloux.plaquette_mido_soap.M1ApprBuilder"; then
		echo "Build succeeded"
	else
   		echo "Build failed"
		BUILT=1
	fi
}

function deploy {
	echo "deploying changes"
	FILES="${FILE_LOG}"
	if test -f "${FILE_PDF}"; then
		FILES="${FILES} ${FILE_PDF}"		
	fi
	mv -f ${FILES} ../${REPO}
	cd ../${REPO}
	git config --global user.name "Travis CI"
    git config --global user.email barnabe.geffroy@psl.eu
	git add ${FILES}
	git status
	git commit -m "Lastest doc built on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to github"
	git push $DEPLOY_REPO master
	exit ${BUILT}
}

main