/*
Physics Engine
  Kinematics Version 8
  Sound
  GUI Simplified
  Dampening
Skanda Koppula
May 2011
*/

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.applet.*;
import javax.sound.sampled.*;

static int[] sizeOfScreen = {800, 600};
float[] sizeOfField = {100, 100};
float accel = -9.8;
float timeDif = 0.005;
float distance = 0, distance1 = 0;
ArrayList<physObject> listObjects = new ArrayList<physObject>();
Mutator m = new Mutator();;
boolean pause = false;
boolean notShowPath = true;
boolean collision = true;
boolean textShow = true;
float damp = 0.95;
float airDens = 1;

long timeEla;
int[] newSPos = new int[4];

boolean auStart = false;
boolean music = true;
File bgMusicFile = new File("BgMusic.wav");
File dingFile = new File("ding.wav");
Clip clip = null;

void startBgMusic() {
  if ((! auStart) && (clip != null)) {
    clip.loop(Clip.LOOP_CONTINUOUSLY);
  }
}

void stopBgMusic() {
  if (auStart && (clip != null)) {
    clip.stop();
  }
}

Clip setupClip(File clipFile) {
  Clip clip = null;

  try {
    AudioInputStream audioIn = AudioSystem.getAudioInputStream(clipFile);
    AudioFormat format = audioIn.getFormat();

    /**
     * we can't yet open the device for ALAW/ULAW playback,
     * convert ALAW/ULAW to PCM
     */
    if ((format.getEncoding() == AudioFormat.Encoding.ULAW) ||
      (format.getEncoding() == AudioFormat.Encoding.ALAW)) {
      AudioFormat tmp = new AudioFormat(
      AudioFormat.Encoding.PCM_SIGNED, 
      format.getSampleRate(), 
      format.getSampleSizeInBits() * 2, 
      format.getChannels(), 
      format.getFrameSize() * 2, 
      format.getFrameRate(), 
      true);
      audioIn = AudioSystem.getAudioInputStream(tmp, audioIn);
      format = tmp;
    }
    DataLine.Info info = new DataLine.Info(Clip.class, audioIn.getFormat(), 
    ((int) audioIn.getFrameLength() * format.getFrameSize()));

    clip = (Clip) AudioSystem.getLine(info);
    clip.addLineListener(new LineListener() {
      public void update(LineEvent event) {
        if (event.getType() == LineEvent.Type.START) {
          auStart = true;
        } 
        else if (event.getType() == LineEvent.Type.STOP) {
          // event.getLine().close();
          auStart = false;
        } 
        //            else {
        //              print ("Encountered error in background music status!\n");
        //            }
      }
    }
    );

    clip.open(audioIn);
  } 
  catch (UnsupportedAudioFileException e) {
    e.printStackTrace();
  } 
  catch (IOException e) {
    e.printStackTrace();
  } 
  catch (LineUnavailableException e) {
    e.printStackTrace();
  }

  return clip;
}

public void makeDing() {
  try {
    AudioInputStream audioIn = AudioSystem.getAudioInputStream(dingFile);

    AudioFormat format = audioIn.getFormat();

    /**
     * we can't yet open the device for ALAW/ULAW playback,
     * convert ALAW/ULAW to PCM
     */
    if ((format.getEncoding() == AudioFormat.Encoding.ULAW) ||
      (format.getEncoding() == AudioFormat.Encoding.ALAW)) {
      AudioFormat tmp = new AudioFormat(
      AudioFormat.Encoding.PCM_SIGNED, 
      format.getSampleRate(), 
      format.getSampleSizeInBits() * 2, 
      format.getChannels(), 
      format.getFrameSize() * 2, 
      format.getFrameRate(), 
      true);
      audioIn = AudioSystem.getAudioInputStream(tmp, audioIn);
      format = tmp;
    }
    DataLine.Info info = new DataLine.Info(
    Clip.class, 
    audioIn.getFormat(), 
    ((int) audioIn.getFrameLength() *
      format.getFrameSize()));

    Clip clip = (Clip) AudioSystem.getLine(info);

    clip.addLineListener(new LineListener() {
      public void update(LineEvent event) {
        if (event.getType() == LineEvent.Type.STOP) {
          // event.getLine().close();
        }
      }
    }
    );

    try {
      clip.open(audioIn);
      clip.start();
      clip.drain();
    } 
    catch (Exception e) {
      e.printStackTrace();
    }
  } 
  catch (UnsupportedAudioFileException e) {
    e.printStackTrace();
  } 
  catch (IOException e) {
    e.printStackTrace();
  } 
  catch (LineUnavailableException e) {
    e.printStackTrace();
  }
}

void setup(){
  clip = setupClip(bgMusicFile);
  Intro.displayIntro();
  size(sizeOfScreen[0], sizeOfScreen[1]);
  resetMap();
  fill(color(0,255,0));
}
int counter = 0;
void draw(){
  if (listObjects.size() > 0&&music) {
    startBgMusic();
  } 
  else if(!music){
    stopBgMusic();
  }
  if(keyPressed&&(key=='n'||key=='a'||key=='f'||key=='t'||key=='p'||key=='h'||key=='r'||key=='l'||key=='c'||key=='e'||key=='m'||key=='d')){
    m.act(key);
  }
  keyPressed = false;
  counter++;
  if(pause){
    delay(100);
    text("Acceleration: " + accel,50,50);
    text("Show Path: " + notShowPath,50,65);
    text("Do Collisions: " + collision,50,80);
    text("Pause: " + pause, 50, 95);
  }
  else{
    delay(round(1000*timeDif));
    updateObjStatus();
    updateDisMap();
    if(collision){
      for(int index = 0; index < listObjects.size(); index++){
        for(int index2 = index+1; index2 < listObjects.size(); index2++){
          distance = distance(listObjects.get(index), listObjects.get(index2));
          if(distance<(listObjects.get(index).getRadius()+listObjects.get(index2).getRadius()-0.1)&&distance!=0){
            if(music)
              makeDing();
            reverseDirection(listObjects.get(index), listObjects.get(index2));
          }
        }
    }
    }
  }
  if(notShowPath&&counter%5==4){
    clearMap();
    updateDisMap();
  }
  if(textShow){
    loadPixels();
    for(int index1 = width*35+50; index1 < width*100+50; index1++){
      if(index1%width>50&&index1%width<200)
        pixels[index1] = color(0, 0, 0);  
    }
    updatePixels();
    text("Acceleration: " + accel,50,50);
    text("Show Path: " + !notShowPath,50,65);
    text("Do Collisions: " + collision,50,80);
    text("Pause: " + pause, 50, 95);
  }
}

void reverseDirection(physObject o1, physObject o2){
   float tempX = o1.getVelX();
   float tempY = o1.getVelY();
   
   makeDing();
   o1.changeVelX(damp*o2.getVelX());
   o1.changeVelY(damp*o2.getVelY());
   o2.changeVelX(damp*tempX);
   o2.changeVelY(damp*tempY);
}

float distance(physObject b1, physObject b2){
  distance1 = sqrt(sq(b1.getX()-b2.getX())+sq(b1.getY()-b2.getY()));
  return distance1;
}

void updateObjStatus(){
  for(int index = 0; index < listObjects.size(); index++){
    physObject o1 = listObjects.get(index);
    o1.updateStatus();
    if(o1.isOutBoundsX()||o1.isOutBoundsY()){
      if(collision==false){
        listObjects.remove(index);
      }
      else if(o1.isOutBoundsX()){
        o1.changeVelX(-damp*o1.getVelX());
        if(music)
          makeDing();
      }
      else{
        o1.changeVelY(-damp*o1.getVelY());
        if(music)
          makeDing();
      }
    }
  }
}

void updateDisMap(){
  for(int index = 0; index < listObjects.size(); index++){
    listObjects.get(index).display();
  }
}

void resetMap(){
  loadPixels();
  for(int index1 = 0; index1 < sizeOfScreen[0]*sizeOfScreen[1]; index1++){
    try{
      pixels[index1] = color(0, 0, 0);
    }
    catch(Exception e){
      print("Oh no!");
    }
  }
  for(int index = 0; index < listObjects.size(); index++)
    listObjects.remove(index);
  updatePixels();
}

void clearMap(){
  loadPixels();
  for(int index1 = 0; index1 < sizeOfScreen[0]*sizeOfScreen[1]; index1++){
    pixels[index1] = color(0, 0, 0);  
  }
  updatePixels();
}

void mousePressed(){
    newSPos[0] = mouseX;
    newSPos[1] = mouseY;
    timeEla = System.nanoTime();
}
void mouseReleased(){
  timeEla = System.nanoTime() - timeEla;
  m.newPhysObjectM(newSPos[0], newSPos[1], pmouseX, pmouseY, timeEla);
}
