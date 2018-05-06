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
		$(SRCDIR)/ShuffleUtils.java \
        $(SRCDIR)/Chromosome.java \
        $(SRCDIR)/Evolution.java \
        $(SRCDIR)/TSP.java 

default: classes

TSP.class: TSP.java Evolution.class Chromosome.class City.class
Evolution.class: Evolution.java Chromosome.class City.class
Chromosome: Chromosome.java ShuffleUtils.class City.class
ShuffleUtils.class: ShuffleUtils.java
City.class: City.java

run:
	$(J) -ea -cp $(BINDIR):$(GUAVA) TSP 100

gui:
	$(J) -ea -cp $(BINDIR):$(GUAVA) TSP 100 y
	
classes: $(CLASSES:.java=.class)
	
clean:
	$(RM) $(OUTDIR)/results.out $(BINDIR)/*.class