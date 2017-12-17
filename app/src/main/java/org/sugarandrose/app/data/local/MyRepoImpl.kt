package org.sugarandrose.app.data.local

import org.sugarandrose.app.injection.scopes.PerApplication

import javax.inject.Inject

@PerApplication
class MyRepoImpl
@Inject
constructor() : MyRepo