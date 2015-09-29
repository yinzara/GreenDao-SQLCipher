package ${schema.defaultJavaPackage}.dagger.component;

import ${schema.appPackage}.dagger.ViewScoped;
import dagger.Subcomponent;
<#list schema.entities as entity>
   <#if entity.generateListView && !entity.skipGenerationEvent>
import ${schema.defaultJavaPackage}.view.Default${entity.className}ListView;
   </#if>
</#list>

@ViewScoped
@Subcomponent
public interface EntityViewComponent {

<#list schema.entities as entity>
   <#if entity.generateListView && !entity.skipGenerationEvent>
   void inject(Default${entity.className}ListView view);
   </#if>
</#list>

}
