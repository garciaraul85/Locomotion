package com.stream.locomotion.domain.usecase

import com.stream.locomotion.domain.model.Program

class SelectProgram {
    operator fun invoke(program: Program): Program = program
}
