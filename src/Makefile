#This Makefile is used to create assignments.

#the following macros should be updated according to
#the assignment to be generated

JARFILES=bufmgr/*.class diskmgr/*.class global/*.class iterator/*.class\
         heap/*.class chainexception/*.class  btree/*.class index/*.class tests/*.class

JDKPATH = /Library/java/JavaVirtualMachines/jdk1.8.0_92.jdk
LIBPATH = .:..
CLASSPATH = $(LIBPATH)
BINPATH = $(JDKPATH)/Contents/Home/bin
JAVAC = $(JDKPATH)/Contents/Home/bin/javac -classpath $(CLASSPATH)
JAVA  = $(JDKPATH)/Contents/Home/bin/java  -classpath $(CLASSPATH)

DOCFILES=bufmgr diskmgr global chainexception heap btree iterator index

##############  update the above for each assignment in making

ASSIGN=/home/javaminibase/src
LIBDIR=$(ASSIGN)/lib
KEY=$(ASSIGN)/key
SRC=$(ASSIGN)/src

IMAGELINK=$(PACKAGEINDEX)/images
PACKAGEINDEX=$(ASSIGN)/javadoc

JAVADOC=javadoc -public -d $(PACKAGEINDEX)

### Generate jar and javadoc files.  Apply to most assignments.
db: 
	make -C global
	make -C chainexception
	make -C btree
	make -C bufmgr
	make -C diskmgr
	make -C heap
	make -C index
	make -C iterator
	
doc:
	$(JAVADOC) $(DOCFILES)

test: 
	# cd tests; make bmtest dbtest; whoami; make hftest bttest indextest jointest sorttest sortmerge test1 test2 test3 test4 test5 test6
	cd tests; make test1; make test2; make test3; make test4; make test5; make test6	

clean:
	\rm -f $(CLASSPATH)/*.class *~ \#* core $(JARFILES) TRACE

