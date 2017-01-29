clear;

addpath('./absor');

path = '/home/doug/Desktop/kinectdata/9-24/frames/';

for i = 0:3
  i
  X = dlmread(strcat(path, "frame", int2str(i), "-", "0.txt"));
  Y = dlmread(strcat(path, "frame", int2str(i), "-", "1.txt"));
  
  [R,T,Yf,Err] = rot3dfit(X, Y);
  
  R'
  T
  Err
  
   [regParams,Bfit,ErrorStats] = absor(X',Y','doTrans', 1);
   regParams.R
   regParams.t
end