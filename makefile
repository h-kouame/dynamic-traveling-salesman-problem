SRCDIR = src
BINDIR = bin

JC = javac
JFLAGS = -g -d $(BINDIR) -cp $(BINDIR)

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        $(SRCDIR)/City.java \
        $(SRCDIR)/Chromosome.java \
        $(SRCDIR)/Evolution.java \
        $(SRCDIR)/TSP.java 

default: classes
	
classes: $(CLASSES:.java=.class)
	
clean:
	$(RM) $(BINDIR)/*.class