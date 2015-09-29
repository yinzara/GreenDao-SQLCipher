package ${schema.defaultJavaPackage}.view.adapter;

import android.support.annotation.LayoutRes;

import ${schema.appPackage}.R;
import ${entity.javaPackageDao}.${entity.classNameDao};
import ${entity.javaPackage}.${entity.className};
import ${schema.defaultJavaPackage}.view.item.${entity.className}ListItemView;
import ${schema.appPackage}.view.base.adapter.BaseEntityBindableViewAdapter;

import java.util.List;

import javax.inject.Inject;

// KEEP INCLUDES - put your custom includes here
<#if keepIncludes?has_content>${keepIncludes!}</#if>// KEEP INCLUDES END

/**
 * Generated by greenDao
 *
 * Created by yinzara on 8/13/15.
 */
public class ${entity.className}ListAdapter extends BaseEntityBindableViewAdapter<${entity.className}, ${entity.className}ListItemView> {


    @Inject
    protected ${entity.classNameDao} _${entity.classNameDao};

    // KEEP FIELDS - put your custom fields here
    ${keepFields!}    // KEEP FIELDS END

    @Inject
    public ${entity.className}ListAdapter() {
        super(R.layout.${entity.tableName?lower_case}_list_item_view);
    }

    public ${entity.className}ListAdapter(@LayoutRes int layoutId) {
            super(layoutId);
        }


    @Override
    protected List<${entity.className}> loadEntities(int offset, int limit) {
        return _${entity.classNameDao}
                .queryBuilder()
                .offset(offset)
                .limit(limit)
                .orderDesc(${entity.classNameDao}.Properties.Id)
                .list();
    }

    // KEEP METHODS - put your custom methods here
    ${keepMethods!}    // KEEP METHODS END

}