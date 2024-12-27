import {ComboBox, CustomField, DatePicker, FormItem, FormLayout, TextArea, TextField, VerticalLayout} from "@vaadin/react-components";
import {translate} from "@vaadin/hilla-react-i18n";
import {useForm, useFormArrayPart, useFormPart} from "@vaadin/hilla-react-form";
import {Button} from "@vaadin/react-components/Button.js";
import {OwnerService, PetService} from "../../../generated/endpoints";
import {useNavigate, useParams} from "react-router-dom";
import {useEffect} from "react";
import {ViewConfig} from "@vaadin/hilla-file-router/types.js";
import ValidationErrors, {handleKeyDown} from "../../../ValidationErrors";
import {useSignal} from "@vaadin/hilla-react-signals";
import PetType from "Frontend/generated/org/springframework/samples/petclinic/backend/owner/PetType";
import OwnerRecordModel from 'Frontend/generated/org/springframework/samples/petclinic/endpoint/record/OwnerRecordModel';
import PetRecordModel from 'Frontend/generated/org/springframework/samples/petclinic/endpoint/record/PetRecordModel';

export const config: ViewConfig = {
    menu: {exclude: true}
};


function PetForm({model, remove, petTypes}: { model: PetRecordModel, remove: () => void, petTypes: PetType[] }) {
    const {field} = useFormPart(model);

    return (
        <FormLayout
            responsiveSteps={[{minWidth: '0', columns: 2}]
            }>
            <FormItem>
                <label slot="label">{translate('name')}</label>
                <TextField {...field(model.name)}></TextField>
            </FormItem>
            <FormItem>
                <label slot="label">{translate('birthDate')}</label>
                <DatePicker {...field(model.birthDate)}></DatePicker>
            </FormItem>
            <FormItem>
                <label slot="label">{translate('type')}</label>
                <ComboBox {...field(model.type)} itemLabelPath="name"
                          items={petTypes}></ComboBox>
            </FormItem>
            <FormItem>
                <label slot="label">{translate('description')}</label>
                <TextArea {...field(model.description)}></TextArea>
            </FormItem>
            <FormItem>
                <Button onClick={remove}>Remove</Button>
            </FormItem>
        </FormLayout>
    );
}

export default function EditOwnerView() {
    const {ownerId} = useParams();
    const navigate = useNavigate();
    const {read, model, submit, field} = useForm(OwnerRecordModel, {
        onSubmit: async (owner) => {
            const savedOwner = await OwnerService.save(owner);
            if (savedOwner) {
                navigate('/owners/' + savedOwner.id);
            }
        }
    });

    const petTypes = useSignal<PetType[]>([]);

    useEffect(() => {
        PetService.findPetTypes().then((data) => {
            petTypes.value = data;
        });
        OwnerService.get(Number(ownerId)).then(read);
    }, [ownerId]);

    const validationErrorSignal = useSignal(null as unknown);

    const submitWithErrors = async () => {
        try {
            validationErrorSignal.value = null;
            await submit();
        } catch (error) {
            validationErrorSignal.value = error;
        }
    }

    const {items, value, setValue} = useFormArrayPart(model.pets);

    return (
        <>
            <VerticalLayout theme="padding spacing"
                            className="w-full justify-center items-stretch">
                <ValidationErrors errors={validationErrorSignal.value}/>
                <FormLayout
                    onKeyDown={(e) => handleKeyDown(e, submitWithErrors)}
                    responsiveSteps={[{minWidth: '0', columns: 1},
                        {minWidth: '600px', columns: 1}]

                    }>
                    <h2>{translate('owner')}</h2>
                    <FormItem>
                        <label slot="label">{translate('firstName')}</label>
                        <TextField {...field(model.firstName)}></TextField>
                    </FormItem>
                    <FormItem>
                        <label slot="label">{translate('lastName')}</label>
                        <TextField {...field(model.lastName)}></TextField>
                    </FormItem>
                    <FormItem>
                        <label slot="label">{translate('address')}</label>
                        <TextField {...field(model.address)}></TextField>
                    </FormItem>
                    <FormItem>
                        <label slot="label">{translate('city')}</label>
                        <TextField {...field(model.city)}></TextField>
                    </FormItem>
                    <FormItem>
                        <label slot="label">{translate('telephone')}</label>
                        <TextField {...field(model.telephone)}></TextField>
                    </FormItem>

                    <FormItem>
                        <label slot="label">{translate('pets')}</label>
                        <CustomField>
                            {items.map((pet, index) => (
                                <PetForm key={index} model={pet} remove={() => setValue(value!.filter((_, i) => i !== index))} petTypes={petTypes.value}/>
                            ))}
                            <Button onClick={() => setValue([...(value ?? []), PetRecordModel.createEmptyValue()])}>Add pet</Button>
                        </CustomField>
                    </FormItem>
                    <FormItem>
                        <Button onClick={submitWithErrors} className="edit-button">{translate('updateOwner')}</Button>
                    </FormItem>
                </FormLayout>
            </VerticalLayout>
        </>
    );
}
