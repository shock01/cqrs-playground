// https://developers.google.com/web/fundamentals/architecture/building-components/customelements#addingmarkup
// https://developers.google.com/web/fundamentals/architecture/building-components/shadowdom
class AuthRegister extends HTMLElement {

    connectedCallback() {
        let shadowRoot = this.attachShadow({mode: 'open'});
        shadowRoot.innerHTML = this.html();
        this.form = shadowRoot.querySelector('form');
        this.form.addEventListener('submit', this);
    }

    handleEvent(event) {
        if (event.type === 'submit') {
            this.validate();
            this.register();
        }
    }

    validate() {
        let {password, retype} = this.form.elements;
        console.dir(password)
        console.log('T', password.value, retype.value);
    }

    async register() {
        let {email, password} = this.form.elements;
        console.log('DO IT', this.form.fields);
        // /registrations
        let registration = {
            email: email.value,
            password: password.value,
            source: 'test'
        };
        // fetch here please
        // and dispatch a custom event when needed

        let response = await fetch('/registrations', {
            method: 'post',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(registration)
        });
        if (response.status === 201) {
            this.dispatchEvent(new CustomEvent('registrationCreated', {detail: {uuid: 'test'}}));
        }
    }

    html() {
        return `<form onsubmit="return false;">
<label>
    <span>email</span>
    <input type="email" name="email">
</label>
<label>
    <span>password</span>
    <input type="password" name="password" required minlength="4">
</label>
<label>
    <span>retype password</span>
    <input type="password" name="retype" required minlength="4">
</label>
<button class="auth-register-btn_register">register</button>
</form>`;
    }
}
window.customElements.define('auth-register', AuthRegister);
