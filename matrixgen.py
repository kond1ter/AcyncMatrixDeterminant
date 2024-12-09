import numpy as np

mtxt = ""
m = np.random.randint(0, 2, size=(17, 17))
# m = np.identity(151)

for row in m:
    mtxt += " ".join([str(x) for x in row]) + "\n"
        
with open("m1.txt", "w", encoding="utf-8") as f:
    f.write(mtxt)

with open("m1det.txt", "w", encoding="utf-8") as f:
    f.write(str(float(np.linalg.det(m))))
