function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment").val();
    commentToTarget(questionId, 1, content);
}

/**
 * 展开和关闭二级评论
 * */
function collapseComments(e) {
    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id);
    if (comments.hasClass("in")) {
        comments.removeClass("in")
        e.classList.remove("active")
    } else {
        var subCommentContainer = $("#comment-" + id);
        if (subCommentContainer.children().length != 2) {
            comments.addClass("in")
            e.classList.add("active")
        } else {
            $.getJSON("/comment/" + id, function (data) {
                $.each(data.data, function (index, comment) {
                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    }).append($("<images/>", {
                        "class": "media-object images-rounded",
                        "src": comment.user.avatarUrl
                    }));
                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body name"
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        "html": comment.user.name
                    })).append($("<div/>", {
                        "html": comment.content
                    })).append($("<div/>", {
                        "class": "menu"
                    })).append($("<span/>", {
                        "class": "pull-right",
                        "html": moment(comment.gmtCreat).format("YYYY-MM-DD"),
                    }));
                    var mediaElement = $("<div/>", {
                        "class": "media"
                    }).append(mediaLeftElement)
                        .append(mediaBodyElement);
                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments",
                    }).append(mediaElement);
                    subCommentContainer.prepend(commentElement);
                });
                comments.addClass("in")
                e.classList.add("active")
            });
        }
    }
}

function commentToTarget(targetId, type, content) {

    if (!content) {
        alert("不能回复空内容");
        return
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {
                window.location.reload();
                // $("#comment_section").hide();
            } else {
                if (response.code == 2004) {
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("https://github.com/login/oauth/authorize?client_id=Iv1.dad09943bd12c973&redirect_uri=http://localhost:8887/callback&scope=user&state=1");
                        window.localStorage.setItem("closable", true);
                    }
                } else {
                    alert(response.message);
                }
            }
            console.log(response);
        },
        dataType: "json"
    });

}

function comment(e) {
    var commentId = e.getAttribute("data-id");
    var content = $("#input-" + commentId).val();
    commentToTarget(commentId, 2, content);

}
function showSelectTag() {
    $("#select-tag").show();
}

function selectTag(e) {
    var value=e.getAttribute("data-tag")
    var previous = $("#tag").val();
    if (previous.indexOf(value) == -1) {
        if (previous) {
            $("#tag").val(previous + "," + value);
        } else {
            $("#tag").val(value);
        }
    }
}