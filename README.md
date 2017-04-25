CustomTabsFragment
==================

### This library is no longer maintained

This library is basically changes fragments on selection of tabs, which are based on ActionBarSherlock 
TabIndicators so you can easily modify them. And it provides a controller class that handles selection, reselection 
events on tab.
	
It was a needy to set tabbar at the bottom of the window, and ActionBarSherlock wouldn't let it so... 

## Usage
Create your layout which hosts a CustomTabHost and a FrameLayout where we will display our fragments. Create your custom tab layout and put whatever component you need. Then, get the instance of CustomTabHost and set its view and counts.

```java
/** Find and customize tab bar */
tabs = (CustomTabHost) findViewById(R.id.tabHolder);
		
// set customView to create items from it
tabs.setCustomViewId(R.layout.custom_tab_item);
		
// set tab count
tabs.setCount(TAB_ITEM_ICONS.length);
```

Create an instance of TabController and notify the instance with tabHost and frameLayout Id that you want to display your fragments in. You have to define tabs tag to be able to reuse the fragments, because library uses add – attach – detach methods of fragmentTransaction instead of replace.

```java
controller = new TabController(this, tabs, R.id.tabContent);
		
// set tabTags on controller to handle with fragment changes
controller.setTabTags(TAB_TAGS);
```

Controller will ask you to pass it fragments up to given position on RequirementListener, you just need to decide which fragment to display on positioned tab and return an instance of it.

```java
// set a fragmentRequeires to return the right fragment on given position
controller.setRequirementListener(new FragmentRequires() {
	@Override
	public Fragment onRequirement(int position) {
		return CustomTabFragment.newInstance(position);
	}
});
```

Finally, don’t forget to initialize fragments

```java
// Initialize fragments with position or without it
controller.InitializeFragmentsWith(0);
```

## Download
Add library dependency to your `build.gradle` file:

[![Maven Central](https://img.shields.io/maven-central/v/com.yayandroid/CustomTabsFragment.svg)](http://search.maven.org/#search%7Cga%7C1%7CCustomTabsFragment)
```groovy
dependencies {
    compile 'com.yayandroid:CustomTabsFragment:1.0'
}
```
