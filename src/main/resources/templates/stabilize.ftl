<#import "lib/wrap.ftl" as wrap>

<@wrap.page>
<div class="container">
    <div class="row">
        <div class="col s6">
            <form method="POST" enctype="multipart/form-data" action="/stabilize/fileUpload" id="form">
                <div class="file-field input-field">
                    <div class="btn">
                        <span>File</span>
                        <input type="file" name="file">
                    </div>
                    <div class="file-path-wrapper">
                        <input class="file-path validate" type="text">
                    </div>
                </div>
                <div class="col s3">
                    <p class="range-field">
                        <span>Smoothing</span>
                        <input type="range" id="smoothing" name="smoothing" min="0" max="100" value="10"/>
                    </p>
                </div>
                <div class="input-field col s12">
                    <select name="crop">
                        <option value="keep">Keep Image Information</option>
                        <option value="black">Black Border</option>
                    </select>
                    <label>Crop - deal with borders that may be visible due to movement compensation</label>
                </div>
                <button class="btn waves-effect waves-light" type="submit" name="action">Upload
                    <i class="material-icons right">send</i>
                </button>
            </form>
        </div>
    </div>
</div>
</div>

<script>
    <#--Initialize Select Box-->
    document.addEventListener('DOMContentLoaded', function () {
        var elems = document.querySelectorAll('select');
        var instances = M.FormSelect.init(elems);
    });
</script>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        document.querySelector('select[name="crop"]').onchange = changeEventHandler;
    }, false);

    function changeEventHandler(event) {
        var optZoom = document.createElement("input");
        if (event.target.value === "black") {
            optZoom.setAttribute("type", "text");
            optZoom.setAttribute("name", "optzoom");
            optZoom.setAttribute("value", "0");
            optZoom.setAttribute("hidden", "true");
            document.getElementById("form").appendChild(optZoom);
        } else {
            var removableInput = document.querySelector('input[name="optzoom"]');
            document.getElementById("form").removeChild(removableInput);
        }
    }
</script>

</@wrap.page>
