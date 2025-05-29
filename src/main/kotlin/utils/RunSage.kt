package com.iskportal.utils

import java.nio.charset.Charset
import io.github.spencerpark.jupyter.kernel.JupyterIO

fun main() {
    val boole=JupyterIO().isAttached
    print(boole)
}