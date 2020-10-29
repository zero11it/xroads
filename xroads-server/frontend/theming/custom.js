const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = ``;

document.head.appendChild($_documentContainer.content);

/* WORKAROUND FOR SAFARI BUG https://github.com/vaadin/vaadin-form-layout/issues/111 */
/*
  FIXME(polymer-modulizer): the above comments were extracted
  from HTML and may be out of place here. Review them and
  then delete this comment!
*/
;

