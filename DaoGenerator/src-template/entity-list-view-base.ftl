/*
 * Copyright 2013 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ${entity.javaPackage}.view.base;

import android.content.Context;
import android.util.AttributeSet;

import ${schema.appPackage}.R;
import ${entity.javaPackage}.${entity.className};
import ${schema.defaultJavaPackageEvent}.${entity.className}Event;
import ${schema.defaultJavaPackageEvent}.${entity.className}ListEvent;
import ${schema.appPackage}.view.base.BaseEntityListView;

import org.androidannotations.annotations.EViewGroup;

import javax.inject.Inject;

@EViewGroup
public abstract class Base${entity.className}ListView extends BaseEntityListView<${entity.className},${entity.className}Event,${entity.className}ListEvent> {

    public Base${entity.className}ListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
