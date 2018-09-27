package com.dharrigan.gh1382.model

import org.hibernate.validator.constraints.Length

data class ContractCreate(

    val id: String,

    @field:Length(min = 1, max = 5)
    val foo: String,

    @field:Length(min = 1, max = 3)
    val bar: String

)
