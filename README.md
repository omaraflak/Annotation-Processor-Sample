# Annotation Processor Example

Simple example to illustrate the use of android annotation processor.

This code will create an annotation named `@Activity` which is meant to target activities. The processor will generate a `Navigator` class with `static` methods to start the annotated activities.

# Creation Steps (Android Studio)

#### 1) File > New > New Module... > Java Library  -> Name it "annotation"
#### 2) File > New > New Module... > Java Library  -> Name it "processor"
#### 3) In processor build.gradle :

    dependencies {
        /*...*/

        implementation project(':annotation')
        implementation 'com.squareup:javapoet:1.9.0'
        implementation 'com.google.auto.service:auto-service:1.0-rc3'
    }
    
#### 4) In app build.gradle :

    dependencies {
        /*...*/

        implementation project(':annotation')
        annotationProcessor project(':processor')
    }
    
 #### 5) Create new file called `Activity.java` in annotation module :
 
    @Target(ElementType.TYPE) // To target classes
    @Retention(RetentionPolicy.SOURCE)
    public @interface Activity {
    }
    
 #### 6) Create new file called `ActivityProcessor.java` in processor module :
 
    @AutoService(Processor.class) // DON'T FORGET THIS
    public class ActivityProcessor extends AbstractProcessor{
        @Override
        public synchronized void init(ProcessingEnvironment processingEnvironment) {
            /* initialize variables */
        }

        @Override
        public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
            /* code generation happens here (using JavaPoet) */
            return false;
        }

        @Override
        public Set<String> getSupportedAnnotationTypes() {
            return Collections.singleton(Activity.class.getCanonicalName());
        }

        @Override
        public SourceVersion getSupportedSourceVersion() {
            return SourceVersion.latestSupported();
        }
    }

#### 7) Annotate any class and Rebuild your project to generate the code

# Sample

* [Annotation](https://github.com/OmarAflak/Annotation-Processor-Sample/blob/master/annotation/src/main/java/me/aflak/annotation/Activity.java)
* [Processor](https://github.com/OmarAflak/Annotation-Processor-Sample/blob/master/processor/src/main/java/me/aflak/processor/ActivityProcessor.java)