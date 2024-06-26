all:

TARGET := cljdebbugs

.PHONY: clean
clean:
	rm -rf target

.PHONY: build
build: clean
	clojure -T:build uberjar

.PHONY: build.native
build.native: target/$(TARGET)

target/$(TARGET): build
	native-image \
		-jar target/$(TARGET)-standalone.jar \
		-H:+ReportExceptionStackTraces \
		--report-unsupported-elements-at-runtime \
		--features=clj_easy.graal_build_time.InitClojureClasses \
		--verbose \
		--no-fallback \
		$@

resources/debbugs.wsdl:
	# see https://wiki.debian.org/DebbugsSoapInterface
	curl -L https://git.savannah.gnu.org/cgit/emacs/elpa.git/plain/Debbugs.wsdl?h=externals/debbugs > $@
