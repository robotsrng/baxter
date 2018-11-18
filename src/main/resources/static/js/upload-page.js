$(".js-upload-btn").click(function () {
    $(".js-sound-table").load("/baxt/upload-page .js-sound-table>*", "");
    $(".js-video-table").load("/baxt/upload-page .js-video-table>*", "");
});

$(".js-remove-file-btn").click(function () {
    $(this).closest('.js-remove-form').submit();
    $(this).closest('.js-file-row').remove();
});
