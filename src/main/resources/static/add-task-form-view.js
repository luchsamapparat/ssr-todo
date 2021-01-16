// @ts-check

import { Backbone } from './backbone.js';
import { customizeValidationErrorMessage, submitForm } from './utils.js';

export const AddTaskFormView = Backbone.View.extend({
    events: {
        'submit': 'onSubmit',
        'change': 'onAddTaskFormChange'
    },

    initialize() {
        this.resetForm();

        customizeValidationErrorMessage(
            this.el.querySelector('.description'),
            'Please enter a task.'
        );
        customizeValidationErrorMessage(
            this.el.querySelector('.due-date'),
            'Please pick a future date.'
        );
    },

    resetForm() {
        this.el.classList.remove('was-validated');
        this.el.reset();
    },

    /**
     * @param {Event} event 
     */
    async onSubmit(event) {
        event.preventDefault();
        const form = /** @type {HTMLFormElement} */ (event.currentTarget);
        await this.addTask(form);
        this.resetForm();
    },

    /**
     * @param {Event} event 
     */
    onAddTaskFormChange(event) {
        const form = /** @type {HTMLFormElement} */ (event.currentTarget);

        form.classList.add('was-validated');
    },

    /**
     * @param {HTMLFormElement} form 
     */
    async addTask(form) {
        const updatedDocument = await submitForm(form);
        this.trigger('taskListUpdate', updatedDocument);
    }
});