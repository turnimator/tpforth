/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.events;

/**
 * When a model needs to give feedback, for instance from a loop.
 * @author atle
 */
public abstract class ProgressEvent {
    public abstract void progress(int max, int min, int i);
}
