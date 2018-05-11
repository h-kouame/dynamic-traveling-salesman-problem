SRCDIR = src
BINDIR = bin
LIB = lib
OUTDIR = output

GUAVA = $(LIB)/com.google.guava_1.6.0.jar

JC = javac
J = java
JFLAGS = -g -d $(BINDIR) -cp $(BINDIR):$(GUAVA)

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        $(SRCDIR)/City.java \
        $(SRCDIR)/Chromosome.java \
        $(SRCDIR)/TSP.java 

default: classes

TSP.class: TSP.java Chromosome.class City.class
Chromosome: Chromosome.java City.class
City.class: City.java

run:
	$(J) -ea -cp $(BINDIR):$(GUAVA) TSP 100

gui:
	$(J) -ea -cp $(BINDIR):$(GUAVA) TSP 100 y
	
classes: $(CLASSES:.java=.class)
	
clean:
	$(RM) $(OUTDIR)/results.out $(BINDIR)/*.class