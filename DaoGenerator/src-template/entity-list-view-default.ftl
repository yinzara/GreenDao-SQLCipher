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

package ${entity.javaPackage}.view;

import android.content.Context;
import android.util.AttributeSet;

import ${schema.appPackage}.R;
import ${schema.appPackage}.view.base.adapter.EntityAdapter;
import ${entity.javaPackage}.view.adapter.${entity.className}ListAdapter;
import ${entity.javaPackage}.${entity.className};
import ${entity.javaPackage}.view.base.Base${entity.className}ListView;
import ${schema.appPackage}.dagger.component.ViewComponent;

import org.androidannotations.annotations.EViewGroup;

import javax.inject.Inject;

@EViewGroup
public class Default${entity.className}ListView extends Base${entity.className}ListView {

    @Inject
    ${entity.className}ListAdapter adapter;

    public Default${entity.className}ListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected EntityAdapter<${entity.className},?> getEntityAdapter() {
        return adapter;
    }

    @Override
    protected void doInject(ViewComponent injector) {
        injector.inject(this);
    }
}
