#!/usr/bin/python
import numpy
import MITData
import sys
def main():
    baseDir = sys.argv[1].strip("/")
    if baseDir[-1] == "/":
        baseDir = baseDir[:-1]
    elevation = sys.argv[2]
    azimuth = sys.argv[3]
    impulse = MITData.mitFullData(baseDir, elevation, azimuth)
    samples = []
    fl = open("pyTests/512_random_samples.txt", 'r')
    for strVal in fl.read().split("\n"):
        try:
            samples.append(int(strVal))
        except ValueError, e:
            pass
    fl.close()
    convolved = numpy.convolve(samples, impulse)
    for value in convolved:
        print value
    
if __name__ == "__main__":
    main()
