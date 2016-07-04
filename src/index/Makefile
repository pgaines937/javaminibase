JDKPATH = ~/.jabba/jdk/1.8.77
LIBPATH = .:..
CLASSPATH = $(LIBPATH)
BINPATH = $(JDKPATH)/Contents/Home/bin
JAVAC = $(JDKPATH)/Contents/Home/bin/javac -classpath $(CLASSPATH)
JAVA  = $(JDKPATH)/Contents/Home/bin/java  -classpath $(CLASSPATH)

PROGS = together

all: $(PROGS)

together:*.java
	$(JAVAC) *.java

clean:
	\rm -f *.class *~ \#* core 
