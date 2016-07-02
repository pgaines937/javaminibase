JDKPATH = /Library/java/JavaVirtualMachines/jdk1.8.0_92.jdk
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
