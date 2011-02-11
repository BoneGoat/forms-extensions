FormsColumnSort v1.0.0
This is an example of implementing column sorting in Oracle Forms. Please go to www.bincsoft.com for more information.

Custom Properties on TextItem
CREATE_HEADER: Call this to create a header above the text field.
Example: Set_Custom_Property(‘EMP.ENAME’, 1, ‘CREATE_HEADER’, ‘EMP.ENAME’);

SORT_MODE: Call this to programmatically set the sorting mode. This will only affect the orientation of the arrow and remove the arrow from other columns.
Example 1: Set_Custom_Property(‘EMP.ENAME’, 1, ‘SORT_MODE’, ‘DESC’); Use this variant when you want to sort one column.
Example 2: Set_Custom_Property(‘EMP.ENAME’, 1, ‘SORT_MODE’, ‘1|DESC’); Use this variant when you want to set sort order on several columns.

Custom Properties on RelayBean
DEBUG: Enable or disable debugging messages.
Example: Set_Custom_Property(‘BLOCK1.BEAN_AREA1’, 1,’DEBUG’, ‘TRUE’);

HEADER_LISTEN: Makes the bean look for new headers. There is really no need to call this unless you’re creating headers dynamically.
Example: Set_Custome_Property(‘BLOCK1.BEAN_AREA1’, 1, ‘HEADER_LISTEN’, ‘’);

HEADER_CLICKED: Event which is raised when a user clicks a header.
Example: See included Forms example (WHEN-CUSTOM-ITEM-EVENT).

EVENT_VALUES: Variable holding the event variables.
Example: See included Forms example (WHEN-CUSTOM-ITEM-EVENT).