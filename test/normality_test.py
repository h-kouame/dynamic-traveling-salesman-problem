from scipy import stats

alpha = 0.05
data1 = []
#with open('../output/results.out') as f:
with open('results.out') as f:
    f.readline()
    while(f.readline()):
        line = f.readline()
        if line != "\n":
            data1.append(eval(line.strip()))
k, p1 = stats.normaltest(data1)

data2 = []
with open('results_s.txt') as f:
    while(f.readline()):
        line = f.readline()
        if line != "\n":
            data2.append(eval(line.strip()))
k, p2 = stats.normaltest(data2)

print "p-value 1: ", p1 
if p1 < alpha: # null hypothesis: data comes from a normal distribution
    print("Data 1 is NOT normally distributed")
else:
    print("Data 1 IS normally distributed")
    
print "p-value 2: ", p2 
if p2 < alpha: # null hypothesis: data comes from a normal distribution
    print("Data 2 is NOT normally distributed")
else:
    print("Data 2 IS normally distributed")

t, p = stats.ttest_ind(data1, data2, equal_var = False)
print
print "---T-test---"
print "t-value: ", t
print "p-value: ", p
if p < alpha: # null hypothesis: No statistical difference
    print("Data 1 and 2 ARE statistically significant different")
else:
    print("Data 1 and 2 are NOT statistically different")
