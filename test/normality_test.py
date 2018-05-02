from scipy import stats

alpha = 0.05
costs = []
with open('../output/results.out') as f:
    f.readline()
    while(f.readline()):
        line = f.readline()
        if line != "\n":
            costs.append(eval(line.strip()))
k, p = stats.normaltest(costs)
print "p-value: ", p 
if p < alpha:
    print("The NOT normally distributed")
else:
    print("The data IS normally distributed")
