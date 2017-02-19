<!DOCTYPE html>
<html>
<head>

    <script type="text/javascript" src="/resources/jquery-3.1.0.min.js"></script>
 <script type="text/javascript" src="/resources/jquery.form.min.js"></script>
<#--  -->

    <script>
        $(function() {
            console.info("asdfasdf");
            $('#product_create_form').ajaxForm({
                type: 'post',
                url: '/products/create?name=pj_zhong',
                dataType: 'json',
                beforeSubmit: function() {
                },
                success: function(result) {
                  console.info(result);
                }
            });

            $('#back').click(function() {
                history.back();
            });
        });
    </script>
</head>
<body>
<div id="content">
    <form id="product_create_form" enctype="multipart/form-data" class="css-form" action="/products/create" method="post">
        <div class="css-form-header">
            <h3>Create Product</h3>
        </div>
        <div class="css-form-row">
            <label for="name">Product name:</label>
            <input type="text" id="name" name="name" class="ext-required">
            <span class="css-color-red">*</span>
        </div>
        <div class="css-form-row">
            <label for="code">Product Code;:</label>
            <input type="text" id="code" name="code" class="ext-required">
            <span class="css-color-red">*</span>
        </div>
        <div class="css-form-row">
            <label for="price">Product price:</label>
            <input type="text" id="price" name="price" class="ext-required">
            <span class="css-color-red">*</span>
        </div>
        <div class="css-form-row">
            <label for="description">Description:</label>
            <textarea id="description" name="description" rows="5"></textarea>
        </div>
        <div class="css-form-row">
            <label for="picture">Picture::</label>
            <input type="file" id="picture" name="picture">
        </div>
        <div class="css-form-footer">
            <button type="submit">save</button>
        </div>
    </form>
</div>
</body>
</html>